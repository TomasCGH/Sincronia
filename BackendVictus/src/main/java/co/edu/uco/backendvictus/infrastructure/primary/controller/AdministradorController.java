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
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/uco-challenge/api/v1/administradores")
public class AdministradorController {

    private final CreateAdministradorUseCase createAdministradorUseCase;
    private final ListAdministradorUseCase listAdministradorUseCase;
    private final UpdateAdministradorUseCase updateAdministradorUseCase;
    private final DeleteAdministradorUseCase deleteAdministradorUseCase;

    public AdministradorController(final CreateAdministradorUseCase createAdministradorUseCase,
                                   final ListAdministradorUseCase listAdministradorUseCase,
                                   final UpdateAdministradorUseCase updateAdministradorUseCase,
                                   final DeleteAdministradorUseCase deleteAdministradorUseCase) {
        this.createAdministradorUseCase = createAdministradorUseCase;
        this.listAdministradorUseCase = listAdministradorUseCase;
        this.updateAdministradorUseCase = updateAdministradorUseCase;
        this.deleteAdministradorUseCase = deleteAdministradorUseCase;
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
                .map(ApiSuccessResponse::of)
                .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
    }

    @GetMapping
    public Mono<ResponseEntity<ApiSuccessResponse<java.util.List<AdministradorResponse>>>> listar() {
        return listAdministradorUseCase.execute().collectList()
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
                .map(ApiSuccessResponse::of)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiSuccessResponse<Void>>> eliminar(@PathVariable("id") final UUID id) {
        return deleteAdministradorUseCase.execute(id)
                .thenReturn(ApiResponseHelper.emptySuccess()) // ✅ sin null ni problemas de genéricos
                .map(ResponseEntity::ok);
    }
}
