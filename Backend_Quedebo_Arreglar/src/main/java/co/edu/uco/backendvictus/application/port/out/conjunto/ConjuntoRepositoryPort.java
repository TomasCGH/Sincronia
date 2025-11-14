package co.edu.uco.backendvictus.application.port.out.conjunto;

import java.util.UUID;

import co.edu.uco.backendvictus.domain.model.conjunto.ConjuntoResidencial;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConjuntoRepositoryPort {

    Mono<ConjuntoResidencial> save(ConjuntoResidencial conjuntoResidencial);

    Mono<ConjuntoResidencial> findById(UUID id);

    Flux<ConjuntoResidencial> findAll();

    Mono<Void> deleteById(UUID id);

    Mono<ConjuntoResidencial> findByCiudadAndNombre(UUID ciudadId, String nombre);

    Flux<ConjuntoResidencial> findAllByTelefono(String telefono);

    Flux<ConjuntoResidencial> findAllWithNames();

    Flux<ConjuntoResidencial> findAllWithNamesPaged(int page, int size);

    Mono<Long> countAll();

    Flux<ConjuntoResidencial> findByDepartamentoId(UUID departamentoId);

    Flux<ConjuntoResidencial> findByCiudadId(UUID ciudadId);

    Flux<ConjuntoResidencial> findByDepartamentoIdAndCiudadId(UUID departamentoId, UUID ciudadId);

    Flux<ConjuntoResidencial> findByNombre(String nombre);
}
