package co.edu.uco.backendvictus.infrastructure.primary.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.backendvictus.application.dto.administrador.AdministradorCreateRequest;
import co.edu.uco.backendvictus.application.dto.administrador.AdministradorResponse;
import co.edu.uco.backendvictus.application.dto.administrador.AdministradorUpdateRequest;
import co.edu.uco.backendvictus.application.usecase.administrador.CreateAdministradorUseCase;
import co.edu.uco.backendvictus.application.usecase.administrador.DeleteAdministradorUseCase;
import co.edu.uco.backendvictus.application.usecase.administrador.ListAdministradorUseCase;
import co.edu.uco.backendvictus.application.usecase.administrador.UpdateAdministradorUseCase;
import co.edu.uco.backendvictus.crosscutting.helpers.DataSanitizer;
import co.edu.uco.backendvictus.infrastructure.primary.response.ApiSuccessResponse;
import co.edu.uco.backendvictus.infrastructure.primary.response.ApiResponseHelper; // 👈 import helper
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import co.edu.uco.backendvictus.application.mapper.AdministradorCatalogMapper;
import co.edu.uco.backendvictus.application.dto.administrador.AdministradorCatalogResponse;
import java.time.Duration;
import org.springframework.http.codec.ServerSentEvent;
import co.edu.uco.backendvictus.application.port.out.administrador.AdministradorEventoPublisher;

@RestController
@RequestMapping("/uco-challenge/api/v1/administradores")
public class AdministradorController {

    private final CreateAdministradorUseCase createAdministradorUseCase;
    private final ListAdministradorUseCase listAdministradorUseCase;
    private final UpdateAdministradorUseCase updateAdministradorUseCase;
    private final DeleteAdministradorUseCase deleteAdministradorUseCase;
    private final AdministradorCatalogMapper catalogMapper;
    private final AdministradorEventoPublisher adminEventoPublisher;
    private static final Duration ADMIN_HEARTBEAT_INTERVAL = Duration.ofSeconds(15);

    public AdministradorController(final CreateAdministradorUseCase createAdministradorUseCase,
                                   final ListAdministradorUseCase listAdministradorUseCase,
                                   final UpdateAdministradorUseCase updateAdministradorUseCase,
                                   final DeleteAdministradorUseCase deleteAdministradorUseCase,
                                   final AdministradorCatalogMapper catalogMapper,
                                   final AdministradorEventoPublisher adminEventoPublisher) {
        this.createAdministradorUseCase = createAdministradorUseCase;
        this.listAdministradorUseCase = listAdministradorUseCase;
        this.updateAdministradorUseCase = updateAdministradorUseCase;
        this.deleteAdministradorUseCase = deleteAdministradorUseCase;
        this.catalogMapper = catalogMapper;
        this.adminEventoPublisher = adminEventoPublisher;
    }

    @PostMapping
    public Mono<ResponseEntity<ApiSuccessResponse<AdministradorResponse>>> crear(
            @RequestBody final AdministradorCreateRequest request) {
        final AdministradorCreateRequest sanitized = new AdministradorCreateRequest(
                DataSanitizer.sanitizeText(request.primerNombre()),
                DataSanitizer.sanitizeText(request.segundoNombres()),
                DataSanitizer.sanitizeText(request.primerApellido()),
                DataSanitizer.sanitizeText(request.segundoApellido()), DataSanitizer.sanitizeText(request.email()),
                DataSanitizer.sanitizeText(request.telefono()));
        return createAdministradorUseCase.execute(sanitized)
                .flatMap(resp -> {
                    final String nombreCompleto = buildNombreCompleto(resp);
                    return adminEventoPublisher.emitCreated(new co.edu.uco.backendvictus.application.dto.administrador.AdministradorCatalogResponse(
                                    resp.id(), nombreCompleto, resp.email(), resp.telefono()))
                            .thenReturn(resp);
                })
                .map(ApiSuccessResponse::of)
                .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
    }

    @GetMapping
    public Mono<ResponseEntity<ApiSuccessResponse<java.util.List<AdministradorCatalogResponse>>>> listar() {
        return listAdministradorUseCase.execute()
                .map(resp -> {
                    final StringBuilder builder = new StringBuilder();
                    builder.append(nullSafe(resp.primerNombre()));
                    if (!isBlank(resp.segundoNombres())) builder.append(' ').append(resp.segundoNombres().trim());
                    builder.append(' ').append(nullSafe(resp.primerApellido()));
                    if (!isBlank(resp.segundoApellido())) builder.append(' ').append(resp.segundoApellido().trim());
                    final String nombreCompleto = builder.toString().replaceAll(" +", " ").trim();
                    return new AdministradorCatalogResponse(resp.id(), nombreCompleto, resp.email(), resp.telefono());
                })
                .collectList()
                .map(ApiSuccessResponse::of)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiSuccessResponse<AdministradorResponse>>> actualizar(
            @PathVariable("id") final UUID id, @RequestBody final AdministradorUpdateRequest request) {
        final AdministradorUpdateRequest sanitized = new AdministradorUpdateRequest(id,
                DataSanitizer.sanitizeText(request.primerNombre()), DataSanitizer.sanitizeText(request.segundoNombres()),
                DataSanitizer.sanitizeText(request.primerApellido()),
                DataSanitizer.sanitizeText(request.segundoApellido()), DataSanitizer.sanitizeText(request.email()),
                DataSanitizer.sanitizeText(request.telefono()));
        return updateAdministradorUseCase.execute(sanitized)
                .flatMap(resp -> {
                    final String nombreCompleto = buildNombreCompleto(resp);
                    return adminEventoPublisher.emitUpdated(new co.edu.uco.backendvictus.application.dto.administrador.AdministradorCatalogResponse(
                                    resp.id(), nombreCompleto, resp.email(), resp.telefono()))
                            .thenReturn(resp);
                })
                .map(ApiSuccessResponse::of)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiSuccessResponse<Void>>> eliminar(@PathVariable("id") final UUID id) {
        return deleteAdministradorUseCase.execute(id)
                .then(adminEventoPublisher.emitDeleted(new co.edu.uco.backendvictus.application.dto.administrador.AdministradorCatalogResponse(
                        id, null, null, null)))
                .thenReturn(ApiResponseHelper.emptySuccess())
                .map(ResponseEntity::ok);
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Map<String, Object>>> streamCatalogo() {
        Flux<ServerSentEvent<Map<String, Object>>> eventos = adminEventoPublisher.stream()
                .map(evt -> ServerSentEvent.<Map<String, Object>>builder()
                        .event(evt.getTipo())
                        .data(Map.of("data", evt.getPayload()))
                        .build());
        Flux<ServerSentEvent<Map<String, Object>>> heartbeat = Flux.interval(ADMIN_HEARTBEAT_INTERVAL)
                .map(seq -> ServerSentEvent.<Map<String, Object>>builder()
                        .event("heartbeat")
                        .comment("keep-alive")
                        .build());
        return Flux.merge(eventos, heartbeat).onBackpressureBuffer();
    }

    private String buildNombreCompleto(final AdministradorResponse resp) {
        final StringBuilder b = new StringBuilder();
        b.append(nullSafe(resp.primerNombre()));
        if (!isBlank(resp.segundoNombres())) b.append(' ').append(resp.segundoNombres().trim());
        b.append(' ').append(nullSafe(resp.primerApellido()));
        if (!isBlank(resp.segundoApellido())) b.append(' ').append(resp.segundoApellido().trim());
        return b.toString().replaceAll(" +", " ").trim();
    }

    private String nullSafe(final String s) { return s == null ? "" : s.trim(); }
    private boolean isBlank(final String s) { return s == null || s.trim().isEmpty(); }
}
