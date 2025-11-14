package co.edu.uco.backendvictus.infrastructure.secondary.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.edu.uco.backendvictus.infrastructure.secondary.entity.PaisEntity;

public interface PaisR2dbcRepository extends ReactiveCrudRepository<PaisEntity, UUID> {
}
