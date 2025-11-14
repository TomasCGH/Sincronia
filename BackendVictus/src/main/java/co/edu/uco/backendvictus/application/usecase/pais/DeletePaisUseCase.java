package co.edu.uco.backendvictus.application.usecase.pais;

import java.util.UUID;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.port.PaisRepository;

@Service
public class DeletePaisUseCase {

    private final PaisRepository repository;

    public DeletePaisUseCase(final PaisRepository repository) {
        this.repository = repository;
    }

    public Mono<Void> execute(final UUID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ApplicationException("Pais no encontrado")))
                .then(repository.deleteById(id));
    }
}
