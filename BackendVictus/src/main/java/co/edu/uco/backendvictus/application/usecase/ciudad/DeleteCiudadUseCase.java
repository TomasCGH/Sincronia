package co.edu.uco.backendvictus.application.usecase.ciudad;

import java.util.UUID;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.port.CiudadRepository;

@Service
public class DeleteCiudadUseCase {

    private final CiudadRepository ciudadRepository;

    public DeleteCiudadUseCase(final CiudadRepository ciudadRepository) {
        this.ciudadRepository = ciudadRepository;
    }

    public Mono<Void> execute(final UUID id) {
        return ciudadRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApplicationException("Ciudad no encontrada")))
                .then(ciudadRepository.deleteById(id));
    }
}
