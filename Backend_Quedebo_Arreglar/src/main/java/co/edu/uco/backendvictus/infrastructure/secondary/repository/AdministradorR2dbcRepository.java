package co.edu.uco.backendvictus.infrastructure.secondary.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.edu.uco.backendvictus.infrastructure.secondary.entity.AdministradorEntity;

public interface AdministradorR2dbcRepository extends ReactiveCrudRepository<AdministradorEntity, UUID> {
}
