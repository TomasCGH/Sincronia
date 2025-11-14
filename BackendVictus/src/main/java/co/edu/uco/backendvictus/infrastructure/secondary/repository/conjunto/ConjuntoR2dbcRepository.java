package co.edu.uco.backendvictus.infrastructure.secondary.repository.conjunto;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.uco.backendvictus.infrastructure.secondary.entity.ConjuntoResidencialEntity;

public interface ConjuntoR2dbcRepository extends ReactiveCrudRepository<ConjuntoResidencialEntity, UUID> {

    Mono<ConjuntoResidencialEntity> findByCiudadIdAndNombre(UUID ciudadId, String nombre);

    @Query("SELECT * FROM conjunto_residencial WHERE telefono = :telefono")
    Flux<ConjuntoResidencialEntity> findAllByTelefono(@Param("telefono") String telefono);

    // Filtrar por ciudad directamente
    Flux<ConjuntoResidencialEntity> findByCiudadId(UUID ciudadId);

    // Filtrar por departamento (requiere join ciudad -> departamento). Usamos query manual.
    @Query("SELECT cr.* FROM conjunto_residencial cr JOIN ciudad c ON cr.ciudad_id = c.id WHERE c.departamento_id = :departamentoId")
    Flux<ConjuntoResidencialEntity> findByDepartamentoId(@Param("departamentoId") UUID departamentoId);

    // Filtrar por ciudad y departamento para validar ambos.
    @Query("SELECT cr.* FROM conjunto_residencial cr JOIN ciudad c ON cr.ciudad_id = c.id WHERE c.id = :ciudadId AND c.departamento_id = :departamentoId")
    Flux<ConjuntoResidencialEntity> findByCiudadIdAndDepartamentoId(@Param("ciudadId") UUID ciudadId, @Param("departamentoId") UUID departamentoId);
}
