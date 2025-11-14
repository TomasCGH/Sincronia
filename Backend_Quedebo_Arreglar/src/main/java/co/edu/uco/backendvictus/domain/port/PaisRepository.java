package co.edu.uco.backendvictus.domain.port;

import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.domain.model.Pais;

public interface PaisRepository {

    Mono<Pais> save(Pais pais);

    Mono<Pais> findById(UUID id);

    Flux<Pais> findAll();

    Mono<Void> deleteById(UUID id);
}
