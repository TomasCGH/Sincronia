package co.edu.uco.backendvictus.application.usecase.administrador;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

import co.edu.uco.backendvictus.application.dto.administrador.AdministradorResponse;
import co.edu.uco.backendvictus.application.mapper.AdministradorApplicationMapper;
import co.edu.uco.backendvictus.domain.port.AdministradorRepository;

@Service
public class ListAdministradorUseCase {

    private final AdministradorRepository administradorRepository;
    private final AdministradorApplicationMapper mapper;

    public ListAdministradorUseCase(final AdministradorRepository administradorRepository,
            final AdministradorApplicationMapper mapper) {
        this.administradorRepository = administradorRepository;
        this.mapper = mapper;
    }

    public Flux<AdministradorResponse> execute() {
        return administradorRepository.findAll().map(mapper::toResponse);
    }
}
