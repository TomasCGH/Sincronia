package co.edu.uco.backendvictus.application.port.out.ciudad;

import co.edu.uco.backendvictus.application.dto.ciudad.CiudadEvento;
import co.edu.uco.backendvictus.application.dto.ciudad.CiudadResponse;
import co.edu.uco.backendvictus.application.dto.evento.TipoEvento;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CiudadEventoPublisher {

    Mono<Void> publish(CiudadEvento evento);

    Flux<CiudadEvento> stream();

    default Mono<Void> emitCreated(final CiudadResponse payload) {
        return publish(CiudadEvento.of(TipoEvento.CREATED, payload));
    }

    default Mono<Void> emitUpdated(final CiudadResponse payload) {
        return publish(CiudadEvento.of(TipoEvento.UPDATED, payload));
    }

    default Mono<Void> emitDeleted(final CiudadResponse payload) {
        return publish(CiudadEvento.of(TipoEvento.DELETED, payload));
    }
}
