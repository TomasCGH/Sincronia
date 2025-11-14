package co.edu.uco.backendvictus.application.usecase.administrador;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.dto.administrador.AdministradorResponse;
import co.edu.uco.backendvictus.application.dto.administrador.AdministradorUpdateRequest;
import co.edu.uco.backendvictus.application.mapper.AdministradorApplicationMapper;
import co.edu.uco.backendvictus.application.usecase.UseCase;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.model.Administrador;
import co.edu.uco.backendvictus.domain.port.AdministradorRepository;

@Service
public class UpdateAdministradorUseCase implements UseCase<AdministradorUpdateRequest, AdministradorResponse> {

    private final AdministradorRepository administradorRepository;
    private final AdministradorApplicationMapper mapper;

    public UpdateAdministradorUseCase(final AdministradorRepository administradorRepository,
            final AdministradorApplicationMapper mapper) {
        this.administradorRepository = administradorRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<AdministradorResponse> execute(final AdministradorUpdateRequest request) {
        return administradorRepository.findById(request.id())
                .switchIfEmpty(Mono.error(new ApplicationException("Administrador no encontrado")))
                .map(existente -> existente.update(request.primerNombre(), request.segundoNombres(),
                        request.primerApellido(), request.segundoApellido(), request.email(), request.telefono()))
                .flatMap(administradorRepository::save)
                .map(mapper::toResponse);
    }
}
