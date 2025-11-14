package co.edu.uco.backendvictus.infrastructure.secondary.repository.conjunto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import co.edu.uco.backendvictus.application.port.out.conjunto.ConjuntoRepositoryPort;
import co.edu.uco.backendvictus.domain.model.Administrador;
import co.edu.uco.backendvictus.domain.model.Ciudad;
import co.edu.uco.backendvictus.domain.model.Departamento;
import co.edu.uco.backendvictus.domain.model.Pais;
import co.edu.uco.backendvictus.domain.model.conjunto.ConjuntoResidencial;
import co.edu.uco.backendvictus.domain.port.AdministradorRepository;
import co.edu.uco.backendvictus.domain.port.CiudadRepository;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.ConjuntoResidencialEntity;
import co.edu.uco.backendvictus.infrastructure.secondary.mapper.ConjuntoResidencialEntityMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ConjuntoRepositoryAdapter implements ConjuntoRepositoryPort {

    private static final String BASE_SELECT = """
            SELECT c.id, c.nombre, c.direccion, c.telefono,
                   c.ciudad_id, c.administrador_id,
                   ci.nombre AS nombre_ciudad,
                   d.id AS departamento_id,
                   d.nombre AS nombre_departamento,
                   p.id AS pais_id,
                   p.nombre AS nombre_pais,
                   a.primer_nombre,
                   a.segundo_nombre,
                   a.primer_apellido,
                   a.segundo_apellido,
                   a.correo,
                   a.telefono AS administrador_telefono
            FROM conjunto_residencial c
            JOIN ciudad ci ON c.ciudad_id = ci.id
            JOIN departamento d ON ci.departamento_id = d.id
            JOIN pais p ON d.pais_id = p.id
            JOIN administrador a ON c.administrador_id = a.id
            """;

    private final ConjuntoR2dbcRepository repository;
    private final ConjuntoResidencialEntityMapper mapper;
    private final CiudadRepository ciudadRepository;
    private final AdministradorRepository administradorRepository;
    private final DatabaseClient databaseClient;

    public ConjuntoRepositoryAdapter(final ConjuntoR2dbcRepository repository,
            final ConjuntoResidencialEntityMapper mapper,
            final CiudadRepository ciudadRepository,
            final AdministradorRepository administradorRepository,
            final DatabaseClient databaseClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.ciudadRepository = ciudadRepository;
        this.administradorRepository = administradorRepository;
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<ConjuntoResidencial> save(final ConjuntoResidencial conjuntoResidencial) {
        return repository.save(mapper.toEntity(conjuntoResidencial))
                .flatMap(this::toDomain)
                .onErrorMap(org.springframework.dao.DataIntegrityViolationException.class,
                        e -> new co.edu.uco.backendvictus.crosscutting.exception.ApplicationException(
                                "El teléfono ya está registrado en otro conjunto residencial", "database-constraint"));
    }

    @Override
    public Mono<ConjuntoResidencial> findById(final UUID id) {
        return repository.findById(id).flatMap(this::toDomain);
    }

    @Override
    public Flux<ConjuntoResidencial> findAll() {
        return repository.findAll().flatMap(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(final UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<ConjuntoResidencial> findByCiudadAndNombre(final UUID ciudadId, final String nombre) {
        return repository.findByCiudadIdAndNombre(ciudadId, nombre)
                .flatMap(this::toDomain);
    }

    @Override
    public Flux<ConjuntoResidencial> findAllByTelefono(final String telefono) {
        return repository.findAllByTelefono(telefono)
                .flatMap(this::toDomain);
    }

    @Override
    public Flux<ConjuntoResidencial> findAllWithNames() {
        return queryConjuntos(BASE_SELECT, Collections.emptyMap());
    }

    @Override
    public Flux<ConjuntoResidencial> findAllWithNamesPaged(final int page, final int size) {
        final int sanitizedPage = Math.max(page, 0);
        final int sanitizedSize = Math.max(1, size);
        Map<String, Object> params = new HashMap<>();
        params.put("limit", sanitizedSize);
        params.put("offset", sanitizedPage * sanitizedSize);
        return queryConjuntos(BASE_SELECT + " ORDER BY c.nombre LIMIT :limit OFFSET :offset", params);
    }

    @Override
    public Mono<Long> countAll() {
        return repository.count();
    }

    @Override
    public Flux<ConjuntoResidencial> findByDepartamentoId(final UUID departamentoId) {
        return queryConjuntos(BASE_SELECT + " WHERE d.id = :departamentoId",
                Map.of("departamentoId", departamentoId));
    }

    @Override
    public Flux<ConjuntoResidencial> findByCiudadId(final UUID ciudadId) {
        return queryConjuntos(BASE_SELECT + " WHERE ci.id = :ciudadId",
                Map.of("ciudadId", ciudadId));
    }

    @Override
    public Flux<ConjuntoResidencial> findByDepartamentoIdAndCiudadId(final UUID departamentoId, final UUID ciudadId) {
        Map<String, Object> params = new HashMap<>();
        params.put("departamentoId", departamentoId);
        params.put("ciudadId", ciudadId);
        return queryConjuntos(BASE_SELECT + " WHERE d.id = :departamentoId AND ci.id = :ciudadId", params);
    }

    @Override
    public Flux<ConjuntoResidencial> findByNombre(final String nombre) {
        return queryConjuntos(BASE_SELECT + " WHERE LOWER(c.nombre) LIKE LOWER(:pattern)",
                Map.of("pattern", "%" + nombre + "%"));
    }

    private Flux<ConjuntoResidencial> queryConjuntos(final String sql, final Map<String, Object> params) {
        DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sql);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            spec = spec.bind(entry.getKey(), entry.getValue());
        }
        return spec.map(this::mapRowToDomain).all();
    }

    private ConjuntoResidencial mapRowToDomain(final Row row, final RowMetadata metadata) {
        final Pais pais = Pais.create(row.get("pais_id", UUID.class), row.get("nombre_pais", String.class));
        final Departamento departamento = Departamento.create(row.get("departamento_id", UUID.class),
                row.get("nombre_departamento", String.class), pais);
        final Ciudad ciudad = Ciudad.create(row.get("ciudad_id", UUID.class), row.get("nombre_ciudad", String.class),
                departamento);
        final Administrador administrador = Administrador.create(row.get("administrador_id", UUID.class),
                row.get("primer_nombre", String.class), row.get("segundo_nombre", String.class),
                row.get("primer_apellido", String.class), row.get("segundo_apellido", String.class),
                row.get("correo", String.class), row.get("administrador_telefono", String.class));
        return ConjuntoResidencial.create(row.get("id", UUID.class), row.get("nombre", String.class),
                row.get("direccion", String.class), ciudad, administrador, row.get("telefono", String.class));
    }

    private Mono<ConjuntoResidencial> toDomain(final ConjuntoResidencialEntity entity) {
        final Mono<co.edu.uco.backendvictus.domain.model.Ciudad> ciudadMono = ciudadRepository
                .findById(entity.getCiudadId());
        final Mono<co.edu.uco.backendvictus.domain.model.Administrador> administradorMono = administradorRepository
                .findById(entity.getAdministradorId());

        return Mono.zip(ciudadMono, administradorMono)
                .map(tuple -> mapper.toDomain(entity, tuple.getT1(), tuple.getT2()));
    }
}
