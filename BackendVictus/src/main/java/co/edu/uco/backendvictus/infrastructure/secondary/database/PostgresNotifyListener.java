package co.edu.uco.backendvictus.infrastructure.secondary.database;

import java.time.Duration;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.uco.backendvictus.application.dto.ciudad.CiudadEvento;
import co.edu.uco.backendvictus.application.dto.ciudad.CiudadResponse;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoEvento;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoResponse;
import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoEvento;
import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoResponse;
import co.edu.uco.backendvictus.application.dto.evento.TipoEvento;
import co.edu.uco.backendvictus.application.port.out.ciudad.CiudadEventoPublisher;
import co.edu.uco.backendvictus.application.port.out.conjunto.ConjuntoEventoPublisher;
import co.edu.uco.backendvictus.application.port.out.departamento.DepartamentoEventoPublisher;
import io.r2dbc.postgresql.api.Notification;
import io.r2dbc.postgresql.api.PostgresqlConnection;
import io.r2dbc.spi.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

// Agregados para Administrador
import co.edu.uco.backendvictus.application.dto.administrador.AdministradorCatalogResponse;
import co.edu.uco.backendvictus.application.dto.administrador.AdministradorEvento;
import co.edu.uco.backendvictus.application.port.out.administrador.AdministradorEventoPublisher;

/**
 * Listener reactivo que se conecta a PostgreSQL usando LISTEN/NOTIFY y re-publica
 * los eventos a los publishers SSE.
 */
@Component
public class PostgresNotifyListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresNotifyListener.class);

    private static final String CH_CONJUNTO = "conjunto_events";
    private static final String CH_DEPARTAMENTO = "departamento_events";
    private static final String CH_CIUDAD = "ciudad_events";
    private static final String CH_ADMIN = "administrador_events";

    private final ConnectionFactory connectionFactory;
    private final ObjectMapper objectMapper;
    private final ConjuntoEventoPublisher conjuntoPublisher;
    private final DepartamentoEventoPublisher departamentoPublisher;
    private final CiudadEventoPublisher ciudadPublisher;
    private final AdministradorEventoPublisher administradorPublisher;

    private Disposable subscription;

    public PostgresNotifyListener(final ConnectionFactory connectionFactory,
            final ObjectMapper objectMapper,
            final ConjuntoEventoPublisher conjuntoPublisher,
            final DepartamentoEventoPublisher departamentoPublisher,
            final CiudadEventoPublisher ciudadPublisher,
            final AdministradorEventoPublisher administradorPublisher) {
        this.connectionFactory = connectionFactory;
        this.objectMapper = objectMapper;
        this.conjuntoPublisher = conjuntoPublisher;
        this.departamentoPublisher = departamentoPublisher;
        this.ciudadPublisher = ciudadPublisher;
        this.administradorPublisher = administradorPublisher;
    }

    @PostConstruct
    public void start() {
        this.subscription = listenFlux()
                .doOnSubscribe(sub -> LOGGER.info("[LISTEN] Suscribiendo a canales NOTIFY"))
                .doOnError(error -> LOGGER.error("[LISTEN] Error en listener", error))
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(2))
                        .maxBackoff(Duration.ofSeconds(30))
                        .jitter(0.2)
                        .doBeforeRetry(rs -> LOGGER.warn("[LISTEN] Reintentando suscripción tras error: {}", rs.failure().toString())))
                .subscribe();
    }

    @PreDestroy
    public void stop() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

    private Flux<Void> listenFlux() {
        return Mono.from(connectionFactory.create())
                .ofType(PostgresqlConnection.class)
                .flatMap(conn -> Mono.when(
                        Mono.from(conn.createStatement("LISTEN " + CH_CONJUNTO).execute()),
                        Mono.from(conn.createStatement("LISTEN " + CH_DEPARTAMENTO).execute()),
                        Mono.from(conn.createStatement("LISTEN " + CH_CIUDAD).execute()),
                        Mono.from(conn.createStatement("LISTEN " + CH_ADMIN).execute()))
                        .thenReturn(conn))
                .flatMapMany(conn -> conn.getNotifications()
                        .doOnSubscribe(sub -> LOGGER.info("[LISTEN] Escuchando canales: {}, {}, {}, {}",
                                CH_CONJUNTO, CH_DEPARTAMENTO, CH_CIUDAD, CH_ADMIN))
                        .doOnError(error -> LOGGER.error("[LISTEN] Error en flujo de notificaciones", error))
                        .doFinally(signalType -> Mono.from(conn.close())
                                .doOnSuccess(ignored -> LOGGER.info("[LISTEN] Conexión LISTEN cerrada ({})", signalType))
                                .onErrorResume(err -> {
                                    LOGGER.warn("[LISTEN] Error cerrando conexión: {}", err.toString());
                                    return Mono.empty();
                                })
                                .subscribe()))
                .flatMap(this::dispatchNotification)
                .onErrorResume(error -> {
                    LOGGER.error("[LISTEN] Error procesando notificación", error);
                    return Mono.empty();
                });
    }

    private Mono<Void> dispatchNotification(final Notification notification) {
        final String channel = notification.getName();
        final String payload = notification.getParameter();
        LOGGER.info("[LISTEN] Notificación recibida en {} -> {}", channel, payload);
        try {
            final JsonNode root = objectMapper.readTree(payload);
            final String tipoRaw = root.path("tipo").asText(null);
            final JsonNode dataNode = root.path("payload");
            if (dataNode.isMissingNode() || dataNode.isNull()) {
                LOGGER.warn("[LISTEN] Payload inválido, no contiene 'payload': {}", payload);
                return Mono.empty();
            }

            return TipoEvento.fromString(tipoRaw)
                    .map(tipo -> routeEvent(channel, tipo, dataNode))
                    .orElseGet(() -> {
                        LOGGER.warn("[LISTEN] Tipo de evento desconocido: {}", tipoRaw);
                        return Mono.empty();
                    });
        } catch (Exception ex) {
            LOGGER.error("[LISTEN] Error parseando payload JSON del canal {}", channel, ex);
            return Mono.empty();
        }
    }

    private Mono<Void> routeEvent(final String channel, final TipoEvento tipo, final JsonNode dataNode) {
        return switch (channel) {
            case CH_CONJUNTO -> {
                final ConjuntoResponse data = objectMapper.convertValue(dataNode, ConjuntoResponse.class);
                yield conjuntoPublisher.publish(ConjuntoEvento.of(tipo, data));
            }
            case CH_DEPARTAMENTO -> {
                final DepartamentoResponse data = objectMapper.convertValue(dataNode, DepartamentoResponse.class);
                yield departamentoPublisher.publish(DepartamentoEvento.of(tipo, data));
            }
            case CH_CIUDAD -> {
                final CiudadResponse data = objectMapper.convertValue(dataNode, CiudadResponse.class);
                yield ciudadPublisher.publish(CiudadEvento.of(tipo, data));
            }
            case CH_ADMIN -> {
                // Construcción de DTO de catálogo desde el row JSON del trigger
                try {
                    final UUID id = dataNode.path("id").isMissingNode() || dataNode.path("id").isNull()
                            ? null : UUID.fromString(dataNode.path("id").asText());
                    final String pNombre = textOrNull(dataNode, "primer_nombre");
                    final String sNombre = textOrNull(dataNode, "segundo_nombre");
                    final String pApellido = textOrNull(dataNode, "primer_apellido");
                    final String sApellido = textOrNull(dataNode, "segundo_apellido");
                    final String correo = textOrNull(dataNode, "correo");
                    final String telefono = textOrNull(dataNode, "telefono");

                    final String nombreCompleto = compactNombre(pNombre, sNombre, pApellido, sApellido);
                    final AdministradorCatalogResponse data = new AdministradorCatalogResponse(
                            id, nombreCompleto, correo, telefono);

                    yield administradorPublisher.publish(new AdministradorEvento(tipo.name(), data));
                } catch (Exception ex) {
                    LOGGER.error("[LISTEN] Error mapeando evento de administrador", ex);
                    yield Mono.empty();
                }
            }
            default -> {
                LOGGER.warn("[LISTEN] Canal no reconocido: {}", channel);
                yield Mono.empty();
            }
        };
    }

    // Utilidades locales para normalizar nombres
    private static String textOrNull(final JsonNode node, final String field) {
        final JsonNode v = node.path(field);
        return (v.isMissingNode() || v.isNull()) ? null : v.asText();
    }

    private static String compactNombre(final String... partes) {
        final StringBuilder sb = new StringBuilder();
        for (String p : partes) {
            if (p != null && !p.isBlank()) {
                if (sb.length() > 0) sb.append(' ');
                sb.append(p.trim());
            }
        }
        return sb.toString().trim();
    }
}
