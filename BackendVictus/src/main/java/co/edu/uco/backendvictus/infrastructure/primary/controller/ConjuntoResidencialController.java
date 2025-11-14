package co.edu.uco.backendvictus.infrastructure.primary.controller;

import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import co.edu.uco.backendvictus.application.dto.common.PageResponse;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoCreateRequest;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoEvento;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoResponse;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoUpdateRequest;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaFilterRequest;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaPageResponse;
import co.edu.uco.backendvictus.application.usecase.conjunto.CreateConjuntoUseCase;
import co.edu.uco.backendvictus.application.usecase.conjunto.DeleteConjuntoUseCase;
import co.edu.uco.backendvictus.application.usecase.conjunto.ListConjuntosUseCase;
import co.edu.uco.backendvictus.application.usecase.conjunto.UpdateConjuntoUseCase;
import co.edu.uco.backendvictus.application.usecase.vivienda.ListViviendaUseCase;
import co.edu.uco.backendvictus.application.port.out.conjunto.ConjuntoEventoPublisher;
import co.edu.uco.backendvictus.crosscutting.helpers.DataSanitizer;
import co.edu.uco.backendvictus.infrastructure.primary.response.ApiSuccessResponse;
import co.edu.uco.backendvictus.infrastructure.primary.response.ApiResponseHelper; // ✅ import helper
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/uco-challenge/api/v1/conjuntos")
public class ConjuntoResidencialController {

    private final CreateConjuntoUseCase createConjuntoUseCase;
    private final ListConjuntosUseCase listConjuntosUseCase;
    private final UpdateConjuntoUseCase updateConjuntoUseCase;
    private final DeleteConjuntoUseCase deleteConjuntoUseCase;
    private final ConjuntoEventoPublisher eventoPublisher;
    private final ListViviendaUseCase listViviendaUseCase;

    public ConjuntoResidencialController(final CreateConjuntoUseCase createConjuntoUseCase,
                                         final ListConjuntosUseCase listConjuntoUseCase,
                                         final UpdateConjuntoUseCase updateConjuntoUseCase,
                                         final DeleteConjuntoUseCase deleteConjuntoUseCase,
                                         final ConjuntoEventoPublisher eventoPublisher,
                                         final ListViviendaUseCase listViviendaUseCase) {
        this.createConjuntoUseCase = createConjuntoUseCase;
        this.listConjuntosUseCase = listConjuntoUseCase;
        this.updateConjuntoUseCase = updateConjuntoUseCase;
        this.deleteConjuntoUseCase = deleteConjuntoUseCase;
        this.eventoPublisher = eventoPublisher;
        this.listViviendaUseCase = listViviendaUseCase;
    }

    @PostMapping
    public Mono<ResponseEntity<ApiSuccessResponse<ConjuntoResponse>>> crear(
            @Valid @RequestBody final ConjuntoCreateRequest request) {

        final ConjuntoCreateRequest sanitized = new ConjuntoCreateRequest(request.ciudadId(), request.administradorId(),
                DataSanitizer.sanitizeText(request.nombre()), DataSanitizer.sanitizeText(request.direccion()),
                DataSanitizer.sanitizeText(request.telefono()));

        return createConjuntoUseCase.execute(sanitized)
                .map(ApiSuccessResponse::of)
                .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
    }

    @GetMapping
    public Mono<PageResponse<ConjuntoResponse>> listar(
            @RequestParam(name = "departamentoId", required = false) final UUID departamentoId,
            @RequestParam(name = "ciudadId", required = false) final UUID ciudadId,
            @RequestParam(name = "nombre", required = false) final String nombre,
            @RequestParam(name = "page", defaultValue = "0") final int page,
            @RequestParam(name = "size", defaultValue = "20") final int size) {
        final String sanitizedNombre = DataSanitizer.sanitizeText(nombre);
        final boolean hasFilters = departamentoId != null || ciudadId != null
                || (sanitizedNombre != null && !sanitizedNombre.isBlank());

        if (hasFilters) {
            Flux<ConjuntoResponse> filtered = listConjuntosUseCase
                    .executeFiltered(departamentoId, ciudadId, sanitizedNombre);
            return listConjuntosUseCase.buildFilteredPage(filtered, page, size);
        }

        return listConjuntosUseCase.executePaged(page, size);
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<ServerSentEvent<ConjuntoEvento>>> streamEventos() {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl("no-cache");
        headers.add("X-Accel-Buffering", "no");
        headers.add("Connection", "keep-alive");

        Flux<ServerSentEvent<ConjuntoEvento>> body = eventoPublisher.stream()
                .map(evento -> ServerSentEvent.<ConjuntoEvento>builder()
                        .event(evento.tipo())
                        .data(evento)
                        .build());

        return ResponseEntity.ok().headers(headers).contentType(MediaType.TEXT_EVENT_STREAM).body(body);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiSuccessResponse<ConjuntoResponse>>> actualizar(
            @PathVariable("id") final UUID id,
            @Valid @RequestBody final ConjuntoUpdateRequest request) {

        final ConjuntoUpdateRequest sanitized = new ConjuntoUpdateRequest(
                id,
                request.ciudadId(),
                request.administradorId(),
                DataSanitizer.sanitizeText(request.nombre()),
                DataSanitizer.sanitizeText(request.direccion()),
                DataSanitizer.sanitizeText(request.telefono()));

        return updateConjuntoUseCase.execute(sanitized)
                .map(ApiSuccessResponse::of)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiSuccessResponse<Void>>> eliminar(@PathVariable("id") final UUID id) {
        return deleteConjuntoUseCase.execute(id)
                .thenReturn(ApiResponseHelper.emptySuccess()) // ✅ sin null ni warnings de tipo
                .map(body -> ResponseEntity.ok(body));
    }

    @GetMapping("/{id}/viviendas")
    public Mono<ResponseEntity<ApiSuccessResponse<ViviendaPageResponse>>> listarViviendas(
            @PathVariable("id") final UUID conjuntoId,
            @RequestParam(name = "page", required = false) final Integer page,
            @RequestParam(name = "size", required = false) final Integer size) {
        return listViviendaUseCase.execute(new ViviendaFilterRequest(conjuntoId, null, null, null, page, size))
                .map(ApiSuccessResponse::of)
                .map(body -> ResponseEntity.ok(body));
    }
}
