package co.edu.uco.backendvictus.application.port.out.conjunto;

import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoEvento;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoResponse;
import co.edu.uco.backendvictus.application.dto.evento.TipoEvento;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConjuntoEventoPublisher {

    Mono<Void> publish(ConjuntoEvento evento);

    Flux<ConjuntoEvento> stream();

    default Mono<Void> emitCreated(final ConjuntoResponse payload) {
        return publish(ConjuntoEvento.of(TipoEvento.CREATED, payload));
    }

    default Mono<Void> emitUpdated(final ConjuntoResponse payload) {
        return publish(ConjuntoEvento.of(TipoEvento.UPDATED, payload));
    }

    default Mono<Void> emitDeleted(final ConjuntoResponse payload) {
        return publish(ConjuntoEvento.of(TipoEvento.DELETED, payload));
    }
}
