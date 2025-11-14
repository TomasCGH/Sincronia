package co.edu.uco.backendvictus.application.usecase.ciudad;

import java.util.UUID;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.port.CiudadEventoPublisher;
import co.edu.uco.backendvictus.application.dto.ciudad.CiudadResponse;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.port.CiudadRepository;

@Service
public class DeleteCiudadUseCase {

    private final CiudadRepository ciudadRepository;
    private final CiudadEventoPublisher eventoPublisher;

    public DeleteCiudadUseCase(final CiudadRepository ciudadRepository,
                               final CiudadEventoPublisher eventoPublisher) {
        this.ciudadRepository = ciudadRepository;
        this.eventoPublisher = eventoPublisher;
    }

    public Mono<Void> execute(final UUID id) {
        return ciudadRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApplicationException("Ciudad no encontrada")))
                .flatMap(ciudad -> {
                    CiudadResponse payload = new CiudadResponse(ciudad.getId(), ciudad.getDepartamento().getId(), ciudad.getNombre());
                    Mono<Void> publish = eventoPublisher != null
                            ? eventoPublisher.publish(new CiudadEventoPublisher.Evento(CiudadEventoPublisher.TipoEvento.DELETED, payload))
                            : Mono.empty();
                    return publish.then(ciudadRepository.deleteById(id));
                });
    }
}
