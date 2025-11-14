package co.edu.uco.backendvictus.infrastructure.secondary.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.edu.uco.backendvictus.domain.model.Ciudad;
import co.edu.uco.backendvictus.domain.port.CiudadRepository;
import co.edu.uco.backendvictus.domain.port.DepartamentoRepository;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.CiudadEntity;
import co.edu.uco.backendvictus.infrastructure.secondary.mapper.CiudadEntityMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class CiudadRepositoryAdapter implements CiudadRepository {

    private final CiudadR2dbcRepository repository;
    private final CiudadEntityMapper mapper;
    private final DepartamentoRepository departamentoRepository;

    public CiudadRepositoryAdapter(final CiudadR2dbcRepository repository, final CiudadEntityMapper mapper,
            final DepartamentoRepository departamentoRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.departamentoRepository = departamentoRepository;
    }

    @Override
    public Mono<Ciudad> save(final Ciudad ciudad) {
        return repository.save(mapper.toEntity(ciudad)).flatMap(this::toDomain);
    }

    @Override
    public Mono<Ciudad> findById(final UUID id) {
        return repository.findById(id).flatMap(this::toDomain);
    }

    @Override
    public Flux<Ciudad> findAll() {
        return repository.findAll().flatMap(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(final UUID id) {
        return repository.deleteById(id);
    }

    private Mono<Ciudad> toDomain(final CiudadEntity entity) {
        return departamentoRepository.findById(entity.getDepartamentoId())
                .map(departamento -> mapper.toDomain(entity, departamento));
    }
}
