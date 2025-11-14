package co.edu.uco.backendvictus.domain.port;

import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.domain.model.Departamento;

public interface DepartamentoRepository {

    Mono<Departamento> save(Departamento departamento);

    Mono<Departamento> findById(UUID id);

    Flux<Departamento> findAll();

    Mono<Void> deleteById(UUID id);
}
