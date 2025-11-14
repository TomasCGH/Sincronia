package co.edu.uco.backendvictus.infrastructure.primary.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.backendvictus.application.dto.ciudad.CiudadCreateRequest;
import co.edu.uco.backendvictus.application.dto.ciudad.CiudadResponse;
import co.edu.uco.backendvictus.application.dto.ciudad.CiudadUpdateRequest;
import co.edu.uco.backendvictus.application.usecase.ciudad.CreateCiudadUseCase;
import co.edu.uco.backendvictus.application.usecase.ciudad.DeleteCiudadUseCase;
import co.edu.uco.backendvictus.application.usecase.ciudad.ListCiudadUseCase;
import co.edu.uco.backendvictus.application.usecase.ciudad.UpdateCiudadUseCase;
import co.edu.uco.backendvictus.crosscutting.helpers.DataSanitizer;
import co.edu.uco.backendvictus.infrastructure.primary.response.ApiSuccessResponse;
import co.edu.uco.backendvictus.infrastructure.primary.response.ApiResponseHelper; // 👈 helper
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/uco-challenge/api/v1/ciudades")
public class CiudadController {

    private final CreateCiudadUseCase createCiudadUseCase;
    private final ListCiudadUseCase listCiudadUseCase;
    private final UpdateCiudadUseCase updateCiudadUseCase;
    private final DeleteCiudadUseCase deleteCiudadUseCase;

    public CiudadController(final CreateCiudadUseCase createCiudadUseCase,
                            final ListCiudadUseCase listCiudadUseCase,
                            final UpdateCiudadUseCase updateCiudadUseCase,
                            final DeleteCiudadUseCase deleteCiudadUseCase) {
        this.createCiudadUseCase = createCiudadUseCase;
        this.listCiudadUseCase = listCiudadUseCase;
        this.updateCiudadUseCase = updateCiudadUseCase;
        this.deleteCiudadUseCase = deleteCiudadUseCase;
    }

    @PostMapping
    public Mono<ResponseEntity<ApiSuccessResponse<CiudadResponse>>> crear(
            @RequestBody final CiudadCreateRequest request) {
        final CiudadCreateRequest sanitized = new CiudadCreateRequest(
                request.departamentoId(),
                DataSanitizer.sanitizeText(request.nombre()));

        return createCiudadUseCase.execute(sanitized)
                .map(ApiSuccessResponse::of)
                .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
    }

    @GetMapping
    public Mono<ResponseEntity<ApiSuccessResponse<java.util.List<CiudadResponse>>>> listar() {
        return listCiudadUseCase.execute()
                .collectList()
                .map(ApiSuccessResponse::of)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiSuccessResponse<CiudadResponse>>> actualizar(
            @PathVariable("id") final UUID id,
            @RequestBody final CiudadUpdateRequest request) {
        final CiudadUpdateRequest sanitized = new CiudadUpdateRequest(
                id,
                request.departamentoId(),
                DataSanitizer.sanitizeText(request.nombre()));

        return updateCiudadUseCase.execute(sanitized)
                .map(ApiSuccessResponse::of)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiSuccessResponse<Void>>> eliminar(@PathVariable("id") final UUID id) {
        return deleteCiudadUseCase.execute(id)
                .thenReturn(ApiResponseHelper.emptySuccess()) // ✅ sin null ni problemas de genéricos
                .map(ResponseEntity::ok);
    }
}
