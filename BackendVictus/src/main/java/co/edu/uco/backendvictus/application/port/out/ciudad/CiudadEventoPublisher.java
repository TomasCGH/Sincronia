package co.edu.uco.backendvictus.application.port.out.ciudad;

import co.edu.uco.backendvictus.application.dto.ciudad.CiudadEvento;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CiudadEventoPublisher {

    Mono<Void> publish(CiudadEvento evento);

    Flux<CiudadEvento> stream();
}
