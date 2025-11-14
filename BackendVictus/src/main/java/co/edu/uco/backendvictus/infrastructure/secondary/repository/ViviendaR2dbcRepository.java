package co.edu.uco.backendvictus.infrastructure.secondary.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.edu.uco.backendvictus.infrastructure.secondary.entity.ViviendaEntity;
import reactor.core.publisher.Mono;

public interface ViviendaR2dbcRepository extends ReactiveCrudRepository<ViviendaEntity, UUID> {

    Mono<ViviendaEntity> findByConjuntoIdAndNumero(UUID conjuntoId, String numero);
}
