package co.edu.uco.backendvictus.domain.port;

import java.util.UUID;

import co.edu.uco.backendvictus.domain.model.Vivienda;
import co.edu.uco.backendvictus.domain.model.ViviendaEstado;
import co.edu.uco.backendvictus.domain.model.ViviendaTipo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ViviendaRepository {

    Mono<Vivienda> save(Vivienda vivienda);

    Mono<Vivienda> findById(UUID id);

    Flux<Vivienda> findByFilters(UUID conjuntoId, ViviendaEstado estado, ViviendaTipo tipo, String numeroLike, int page,
            int size);

    Mono<Long> countByFilters(UUID conjuntoId, ViviendaEstado estado, ViviendaTipo tipo, String numeroLike);

    Mono<Vivienda> findByConjuntoAndNumero(UUID conjuntoId, String numero);

    Mono<Void> deleteById(UUID id);
}
