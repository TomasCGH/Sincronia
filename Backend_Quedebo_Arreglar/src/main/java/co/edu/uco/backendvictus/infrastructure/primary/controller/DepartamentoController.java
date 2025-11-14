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

import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoCreateRequest;
import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoResponse;
import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoUpdateRequest;
import co.edu.uco.backendvictus.application.usecase.departamento.CreateDepartamentoUseCase;
import co.edu.uco.backendvictus.application.usecase.departamento.DeleteDepartamentoUseCase;
import co.edu.uco.backendvictus.application.usecase.departamento.ListDepartamentoUseCase;
import co.edu.uco.backendvictus.application.usecase.departamento.UpdateDepartamentoUseCase;
import co.edu.uco.backendvictus.crosscutting.helpers.DataSanitizer;
import co.edu.uco.backendvictus.infrastructure.primary.response.ApiSuccessResponse;
import co.edu.uco.backendvictus.infrastructure.primary.response.ApiResponseHelper; // 👈
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/uco-challenge/api/v1/departamentos")
public class DepartamentoController {

    private final CreateDepartamentoUseCase createDepartamentoUseCase;
    private final ListDepartamentoUseCase listDepartamentoUseCase;
    private final UpdateDepartamentoUseCase updateDepartamentoUseCase;
    private final DeleteDepartamentoUseCase deleteDepartamentoUseCase;

    public DepartamentoController(final CreateDepartamentoUseCase createDepartamentoUseCase,
                                  final ListDepartamentoUseCase listDepartamentoUseCase,
                                  final UpdateDepartamentoUseCase updateDepartamentoUseCase,
                                  final DeleteDepartamentoUseCase deleteDepartamentoUseCase) {
        this.createDepartamentoUseCase = createDepartamentoUseCase;
        this.listDepartamentoUseCase = listDepartamentoUseCase;
        this.updateDepartamentoUseCase = updateDepartamentoUseCase;
        this.deleteDepartamentoUseCase = deleteDepartamentoUseCase;
    }

    @PostMapping
    public Mono<ResponseEntity<ApiSuccessResponse<DepartamentoResponse>>> crear(
            @RequestBody final DepartamentoCreateRequest request) {
        final DepartamentoCreateRequest sanitized = new DepartamentoCreateRequest(request.paisId(),
                DataSanitizer.sanitizeText(request.nombre()));
        return createDepartamentoUseCase.execute(sanitized)
                .map(ApiSuccessResponse::of)
                .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
    }

    @GetMapping
    public Mono<ResponseEntity<ApiSuccessResponse<java.util.List<DepartamentoResponse>>>> listar() {
        return listDepartamentoUseCase.execute().collectList()
                .map(ApiSuccessResponse::of)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiSuccessResponse<DepartamentoResponse>>> actualizar(
            @PathVariable("id") final UUID id, @RequestBody final DepartamentoUpdateRequest request) {
        final DepartamentoUpdateRequest sanitized = new DepartamentoUpdateRequest(id, request.paisId(),
                DataSanitizer.sanitizeText(request.nombre()));
        return updateDepartamentoUseCase.execute(sanitized)
                .map(ApiSuccessResponse::of)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiSuccessResponse<Void>>> eliminar(@PathVariable("id") final UUID id) {
        return deleteDepartamentoUseCase.execute(id)
                .thenReturn(ApiResponseHelper.emptySuccess()) // ✅
                .map(ResponseEntity::ok);
    }
}
