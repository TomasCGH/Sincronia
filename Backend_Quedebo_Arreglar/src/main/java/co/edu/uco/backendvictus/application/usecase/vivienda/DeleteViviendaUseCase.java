package co.edu.uco.backendvictus.application.usecase.vivienda;

import java.util.UUID;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.port.ViviendaRepository;

@Service
public class DeleteViviendaUseCase {

    private final ViviendaRepository viviendaRepository;

    public DeleteViviendaUseCase(final ViviendaRepository viviendaRepository) {
        this.viviendaRepository = viviendaRepository;
    }

    public Mono<Void> execute(final UUID id) {
        return viviendaRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApplicationException("Vivienda no encontrada")))
                .then(viviendaRepository.deleteById(id));
    }
}
