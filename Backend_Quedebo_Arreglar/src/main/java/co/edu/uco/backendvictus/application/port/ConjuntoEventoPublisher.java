package co.edu.uco.backendvictus.application.port;

import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface ConjuntoEventoPublisher {

    enum TipoEvento { CREATED, UPDATED, DELETED }

    record Evento(TipoEvento tipo, ConjuntoResponse data) {}

    Mono<Void> publish(Evento evento);
    Flux<Evento> stream();
}

