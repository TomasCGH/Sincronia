package co.edu.uco.backendvictus.application.port.out.conjunto;

import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoEvento;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConjuntoEventoPublisher {

    Mono<Void> publish(ConjuntoEvento evento);

    Flux<ConjuntoEvento> stream();
}
