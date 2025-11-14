package co.edu.uco.backendvictus.domain.port;

import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.domain.model.Administrador;

public interface AdministradorRepository {

    Mono<Administrador> save(Administrador administrador);

    Mono<Administrador> findById(UUID id);

    Flux<Administrador> findAll();

    Mono<Void> deleteById(UUID id);
}
