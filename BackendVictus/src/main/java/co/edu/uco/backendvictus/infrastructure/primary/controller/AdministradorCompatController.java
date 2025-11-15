package co.edu.uco.backendvictus.infrastructure.primary.controller;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.backendvictus.application.dto.administrador.AdministradorCatalogResponse;
import co.edu.uco.backendvictus.application.dto.administrador.AdministradorResponse;
import co.edu.uco.backendvictus.application.port.out.administrador.AdministradorEventoPublisher;
import co.edu.uco.backendvictus.application.usecase.administrador.ListAdministradorUseCase;
import co.edu.uco.backendvictus.infrastructure.primary.response.ApiSuccessResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/administradores")
public class AdministradorCompatController {

    private static final Duration HEARTBEAT_INTERVAL = Duration.ofSeconds(15);

    private final ListAdministradorUseCase listAdministradorUseCase;
    private final AdministradorEventoPublisher adminEventoPublisher;

    public AdministradorCompatController(final ListAdministradorUseCase listAdministradorUseCase,
                                         final AdministradorEventoPublisher adminEventoPublisher) {
        this.listAdministradorUseCase = listAdministradorUseCase;
        this.adminEventoPublisher = adminEventoPublisher;
    }

    @GetMapping
    public Mono<ResponseEntity<ApiSuccessResponse<java.util.List<AdministradorCatalogResponse>>>> listar() {
        return listAdministradorUseCase.execute()
                .map(this::toCatalog)
                .collectList()
                .map(ApiSuccessResponse::of)
                .map(ResponseEntity::ok);
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Map<String, Object>>> stream() {
        Flux<ServerSentEvent<Map<String, Object>>> eventos = adminEventoPublisher.stream()
                .map(evt -> ServerSentEvent.<Map<String, Object>>builder()
                        .event(evt.getTipo())
                        .data(Map.of("data", evt.getPayload()))
                        .build());
        Flux<ServerSentEvent<Map<String, Object>>> heartbeat = Flux.interval(HEARTBEAT_INTERVAL)
                .map(seq -> ServerSentEvent.<Map<String, Object>>builder()
                        .event("heartbeat")
                        .comment("keep-alive")
                        .build());
        return Flux.merge(eventos, heartbeat).onBackpressureBuffer();
    }

    private AdministradorCatalogResponse toCatalog(final AdministradorResponse resp) {
        final StringBuilder builder = new StringBuilder();
        builder.append(nullSafe(resp.primerNombre()));
        if (!isBlank(resp.segundoNombres())) builder.append(' ').append(resp.segundoNombres().trim());
        builder.append(' ').append(nullSafe(resp.primerApellido()));
        if (!isBlank(resp.segundoApellido())) builder.append(' ').append(resp.segundoApellido().trim());
        final String nombreCompleto = builder.toString().replaceAll(" +", " ").trim();
        return new AdministradorCatalogResponse(resp.id(), nombreCompleto, resp.email(), resp.telefono());
    }

    private static String nullSafe(final String s) { return s == null ? "" : s.trim(); }
    private static boolean isBlank(final String s) { return s == null || s.trim().isEmpty(); }
}
