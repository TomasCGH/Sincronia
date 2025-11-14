package co.edu.uco.backendvictus.domain.port;

import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.domain.model.Ciudad;

public interface CiudadRepository {

    Mono<Ciudad> save(Ciudad ciudad);

    Mono<Ciudad> findById(UUID id);

    Flux<Ciudad> findAll();

    Mono<Void> deleteById(UUID id);
}
