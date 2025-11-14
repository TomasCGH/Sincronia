package co.edu.uco.backendvictus.infrastructure.secondary.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.edu.uco.backendvictus.infrastructure.secondary.entity.CiudadEntity;

public interface CiudadR2dbcRepository extends ReactiveCrudRepository<CiudadEntity, UUID> {
}
