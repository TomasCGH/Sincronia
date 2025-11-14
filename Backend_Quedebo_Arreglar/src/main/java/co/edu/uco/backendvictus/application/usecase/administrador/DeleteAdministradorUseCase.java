package co.edu.uco.backendvictus.application.usecase.administrador;

import java.util.UUID;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.port.AdministradorRepository;

@Service
public class DeleteAdministradorUseCase {

    private final AdministradorRepository administradorRepository;

    public DeleteAdministradorUseCase(final AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    public Mono<Void> execute(final UUID id) {
        return administradorRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApplicationException("Administrador no encontrado")))
                .then(administradorRepository.deleteById(id));
    }
}
