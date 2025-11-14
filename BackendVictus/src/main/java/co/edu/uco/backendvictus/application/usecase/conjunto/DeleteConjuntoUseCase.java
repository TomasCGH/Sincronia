package co.edu.uco.backendvictus.application.usecase.conjunto;

import java.util.UUID;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoEvento;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoResponse;
import co.edu.uco.backendvictus.application.port.out.conjunto.ConjuntoEventoPublisher;
import co.edu.uco.backendvictus.application.port.out.conjunto.ConjuntoRepositoryPort;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;

@Service
public class DeleteConjuntoUseCase {

    private final ConjuntoRepositoryPort conjuntoRepository;
    private final ConjuntoEventoPublisher eventoPublisher;

    public DeleteConjuntoUseCase(final ConjuntoRepositoryPort conjuntoRepository,
            final ConjuntoEventoPublisher eventoPublisher) {
        this.conjuntoRepository = conjuntoRepository;
        this.eventoPublisher = eventoPublisher;
    }

    public Mono<Void> execute(final UUID id) {
        return conjuntoRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApplicationException("Conjunto residencial no encontrado")))
                .flatMap(conjunto -> {
                    ConjuntoResponse payload = new ConjuntoResponse(conjunto.getId(), conjunto.getCiudad().getId(),
                            conjunto.getAdministrador().getId(), conjunto.getNombre(), conjunto.getDireccion(),
                            conjunto.getTelefono(), conjunto.getCiudad().getNombre(),
                            conjunto.getCiudad().getDepartamento().getNombre());
                    return eventoPublisher.publish(new ConjuntoEvento("DELETED", payload))
                            .then(conjuntoRepository.deleteById(id));
                });
    }
}
