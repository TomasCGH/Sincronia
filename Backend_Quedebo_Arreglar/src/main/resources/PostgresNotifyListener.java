package co.edu.uco.backendvictus.infrastructure.secondary.database;

import co.edu.uco.backendvictus.application.dto.ciudad.CiudadResponse;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoEvento;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoResponse;
import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoResponse;
import co.edu.uco.backendvictus.application.port.CiudadEventoPublisher;
import co.edu.uco.backendvictus.application.port.DepartamentoEventoPublisher;
import co.edu.uco.backendvictus.application.port.out.conjunto.ConjuntoEventoPublisher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.api.Notification;
import io.r2dbc.postgresql.api.PostgresqlConnection;
import io.r2dbc.spi.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.time.Duration;
import java.util.Locale;

/**
 * Listener reactivo para canales LISTEN/NOTIFY de PostgreSQL que reenvía eventos a los publishers SSE.
 * Canales: conjunto_events, departamento_events, ciudad_events, administrador_events (solo log si no hay publisher).
 */
@Component
public class PostgresNotifyListener {

    private static final Logger log = LoggerFactory.getLogger(PostgresNotifyListener.class);

    private static final String CH_CONJUNTO = "conjunto_events";
    private static final String CH_DEPARTAMENTO = "departamento_events";
    private static final String CH_CIUDAD = "ciudad_events";
    private static final String CH_ADMIN = "administrador_events";

    private final ConnectionFactory connectionFactory;
    private final ObjectMapper objectMapper;
    private final ConjuntoEventoPublisher conjuntoPublisher;
    private final DepartamentoEventoPublisher departamentoPublisher;
    private final CiudadEventoPublisher ciudadPublisher;

    private Disposable subscription;

    public PostgresNotifyListener(
            final ConnectionFactory connectionFactory,
            final ObjectMapper objectMapper,
            final ConjuntoEventoPublisher conjuntoPublisher,
            final DepartamentoEventoPublisher departamentoPublisher,
            final CiudadEventoPublisher ciudadPublisher) {
        this.connectionFactory = connectionFactory;
        this.objectMapper = objectMapper;
        this.conjuntoPublisher = conjuntoPublisher;
        this.departamentoPublisher = departamentoPublisher;
        this.ciudadPublisher = ciudadPublisher;
    }

    @PostConstruct
    public void start() {
        // Suscribirse con reintentos exponenciales para resiliencia
        this.subscription = listenFlux()
                .doOnSubscribe(s -> log.info("[LISTEN] Suscribiendo a canales NOTIFY en PostgreSQL..."))
                .doOnError(err -> log.error("[LISTEN] Error en listener: {}", err.toString(), err))
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(2))
                        .maxBackoff(Duration.ofSeconds(30))
                        .jitter(0.2)
                        .doBeforeRetry(rs -> log.warn("[LISTEN] Reintentando suscripción después de error (intent {}): {}",
                                rs.totalRetries() + 1, rs.failure().toString())))
                .subscribe();
    }

    @PreDestroy
    public void stop() {
        if (this.subscription != null && !this.subscription.isDisposed()) {
            this.subscription.dispose();
        }
    }

    private Flux<Void> listenFlux() {
        return Mono.from(connectionFactory.create())
                .cast(PostgresqlConnection.class)
                .flatMap(conn -> Mono.when(
                                Mono.from(conn.createStatement("LISTEN " + CH_CONJUNTO).execute()),
                                Mono.from(conn.createStatement("LISTEN " + CH_DEPARTAMENTO).execute()),
                                Mono.from(conn.createStatement("LISTEN " + CH_CIUDAD).execute()),
                                Mono.from(conn.createStatement("LISTEN " + CH_ADMIN).execute())
                        )
                        .thenReturn(conn))
                .flatMapMany(conn -> conn.getNotifications()
                        .doOnSubscribe(s -> log.info("[LISTEN] Conexión abierta y escuchando canales: {}, {}, {}, {}",
                                CH_CONJUNTO, CH_DEPARTAMENTO, CH_CIUDAD, CH_ADMIN))
                        .doOnError(e -> log.error("[LISTEN] Error en flujo de notificaciones: {}", e.toString(), e))
                        .doFinally(st -> Mono.from(conn.close())
                                .doOnSuccess(v -> log.info("[LISTEN] Conexión LISTEN cerrada (señal: {})", st))
                                .onErrorResume(err -> {
                                    log.warn("[LISTEN] Error cerrando conexión: {}", err.toString());
                                    return Mono.empty();
                                })
                                .subscribe())
                )
                .flatMap(this::dispatchNotification)
                .onErrorResume(err -> {
                    log.error("[LISTEN] Error procesando notificación: {}", err.toString(), err);
                    return Mono.empty();
                });
    }

    private Mono<Void> dispatchNotification(final Notification notification) {
        final String channel = notification.getName();
        final String payload = notification.getParameter();
        log.info("[LISTEN] Notificación recibida en canal {}: payload {}", channel, payload);

        try {
            final JsonNode root = objectMapper.readTree(payload);
            final String tipo = root.path("tipo").asText(null);
            final JsonNode dataNode = root.path("data");
            if (tipo == null || dataNode.isMissingNode() || dataNode.isNull()) {
                log.warn("[LISTEN] Payload inválido, faltan campos 'tipo' o 'data': {}", payload);
                return Mono.empty();
            }

            switch (channel) {
                case CH_CONJUNTO -> {
                    final ConjuntoResponse data = objectMapper.convertValue(dataNode, ConjuntoResponse.class);
                    final ConjuntoEvento evento = new ConjuntoEvento(tipo, data);
                    return conjuntoPublisher.publish(evento);
                }
                case CH_DEPARTAMENTO -> {
                    final DepartamentoResponse data = objectMapper.convertValue(dataNode, DepartamentoResponse.class);
                    final DepartamentoEventoPublisher.TipoEvento t = toDepartamentoTipo(tipo);
                    final DepartamentoEventoPublisher.Evento evento = new DepartamentoEventoPublisher.Evento(t, data);
                    return departamentoPublisher.publish(evento);
                }
                case CH_CIUDAD -> {
                    final CiudadResponse data = objectMapper.convertValue(dataNode, CiudadResponse.class);
                    final CiudadEventoPublisher.TipoEvento t = toCiudadTipo(tipo);
                    final CiudadEventoPublisher.Evento evento = new CiudadEventoPublisher.Evento(t, data);
                    return ciudadPublisher.publish(evento);
                }
                case CH_ADMIN -> {
                    // No existe publisher de Administrador. Solo se registran logs.
                    log.info("[LISTEN] Evento de administrador recibido (sin publisher configurado): {}", payload);
                    return Mono.empty();
                }
                default -> {
                    log.warn("[LISTEN] Canal no reconocido: {}", channel);
                    return Mono.empty();
                }
            }
        } catch (Exception e) {
            log.error("[LISTEN] Error parseando payload JSON del canal {}: {}", channel, e.toString(), e);
            return Mono.empty();
        }
    }

    private DepartamentoEventoPublisher.TipoEvento toDepartamentoTipo(final String tipo) {
        return DepartamentoEventoPublisher.TipoEvento.valueOf(tipo.toUpperCase(Locale.ROOT));
    }

    private CiudadEventoPublisher.TipoEvento toCiudadTipo(final String tipo) {
        return CiudadEventoPublisher.TipoEvento.valueOf(tipo.toUpperCase(Locale.ROOT));
    }
}

