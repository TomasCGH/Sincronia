package co.edu.uco.backendvictus.infrastructure.primary.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaChangeEstadoRequest;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaCreateRequest;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaFilterRequest;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaPageResponse;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaResponse;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaUpdateRequest;
import co.edu.uco.backendvictus.application.usecase.vivienda.ChangeViviendaEstadoUseCase;
import co.edu.uco.backendvictus.application.usecase.vivienda.CreateViviendaUseCase;
import co.edu.uco.backendvictus.application.usecase.vivienda.DeleteViviendaUseCase;
import co.edu.uco.backendvictus.application.usecase.vivienda.ListViviendaUseCase;
import co.edu.uco.backendvictus.application.usecase.vivienda.UpdateViviendaUseCase;
import co.edu.uco.backendvictus.crosscutting.helpers.DataSanitizer;
import co.edu.uco.backendvictus.infrastructure.primary.response.ApiSuccessResponse;
import co.edu.uco.backendvictus.infrastructure.primary.response.ApiResponseHelper; // ✅ import helper
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/uco-challenge/api/v1/viviendas")
public class ViviendaController {

    private final CreateViviendaUseCase createViviendaUseCase;
    private final ListViviendaUseCase listViviendaUseCase;
    private final UpdateViviendaUseCase updateViviendaUseCase;
    private final ChangeViviendaEstadoUseCase changeViviendaEstadoUseCase;
    private final DeleteViviendaUseCase deleteViviendaUseCase;

    public ViviendaController(final CreateViviendaUseCase createViviendaUseCase,
                              final ListViviendaUseCase listViviendaUseCase,
                              final UpdateViviendaUseCase updateViviendaUseCase,
                              final ChangeViviendaEstadoUseCase changeViviendaEstadoUseCase,
                              final DeleteViviendaUseCase deleteViviendaUseCase) {
        this.createViviendaUseCase = createViviendaUseCase;
        this.listViviendaUseCase = listViviendaUseCase;
        this.updateViviendaUseCase = updateViviendaUseCase;
        this.changeViviendaEstadoUseCase = changeViviendaEstadoUseCase;
        this.deleteViviendaUseCase = deleteViviendaUseCase;
    }

    @PostMapping
    public Mono<ResponseEntity<ApiSuccessResponse<ViviendaResponse>>> crear(
            @RequestBody final ViviendaCreateRequest request) {
        final ViviendaCreateRequest sanitized = new ViviendaCreateRequest(
                request.conjuntoId(),
                DataSanitizer.sanitizeText(request.numero()),
                cleanText(request.tipo()),
                cleanText(request.estado()));

        return createViviendaUseCase.execute(sanitized)
                .map(ApiSuccessResponse::of)
                .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
    }

    @GetMapping
    public Mono<ResponseEntity<ApiSuccessResponse<ViviendaPageResponse>>> listar(
            @RequestParam(name = "conjuntoId", required = false) final UUID conjuntoId,
            @RequestParam(name = "estado", required = false) final String estado,
            @RequestParam(name = "tipo", required = false) final String tipo,
            @RequestParam(name = "numero", required = false) final String numero,
            @RequestParam(name = "page", required = false) final Integer page,
            @RequestParam(name = "size", required = false) final Integer size) {

        final ViviendaFilterRequest filter = new ViviendaFilterRequest(
                conjuntoId,
                cleanText(estado),
                cleanText(tipo),
                cleanText(numero),
                page,
                size);

        return listViviendaUseCase.execute(filter)
                .map(ApiSuccessResponse::of)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiSuccessResponse<ViviendaResponse>>> actualizar(
            @PathVariable("id") final UUID id,
            @RequestBody final ViviendaUpdateRequest request) {

        final ViviendaUpdateRequest sanitized = new ViviendaUpdateRequest(
                id,
                request.conjuntoId(),
                DataSanitizer.sanitizeText(request.numero()),
                cleanText(request.tipo()),
                cleanText(request.estado()));

        return updateViviendaUseCase.execute(sanitized)
                .map(ApiSuccessResponse::of)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}/estado")
    public Mono<ResponseEntity<ApiSuccessResponse<ViviendaResponse>>> cambiarEstado(
            @PathVariable("id") final UUID id,
            @RequestBody final ViviendaChangeEstadoRequest request) {

        final ViviendaChangeEstadoRequest sanitized =
                new ViviendaChangeEstadoRequest(id, cleanText(request.estado()));

        return changeViviendaEstadoUseCase.execute(sanitized)
                .map(ApiSuccessResponse::of)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiSuccessResponse<Void>>> eliminar(@PathVariable("id") final UUID id) {
        return deleteViviendaUseCase.execute(id)
                .thenReturn(ApiResponseHelper.emptySuccess()) // ✅ sin null
                .map(ResponseEntity::ok);
    }

    private static String cleanText(final String value) {
        final String sanitized = DataSanitizer.sanitizeText(value);
        return sanitized == null || sanitized.isBlank() ? null : sanitized;
    }
}
