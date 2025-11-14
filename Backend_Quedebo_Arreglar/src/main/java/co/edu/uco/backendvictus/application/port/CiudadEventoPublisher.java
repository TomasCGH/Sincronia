package co.edu.uco.backendvictus.application.port;

import co.edu.uco.backendvictus.application.dto.ciudad.CiudadResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CiudadEventoPublisher {
    enum TipoEvento { CREATED, UPDATED, DELETED }
    record Evento(TipoEvento tipo, CiudadResponse data) {}
    Mono<Void> publish(Evento evento);
    Flux<Evento> stream();
}
