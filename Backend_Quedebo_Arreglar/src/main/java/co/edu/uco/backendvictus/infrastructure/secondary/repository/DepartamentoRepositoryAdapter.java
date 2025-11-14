package co.edu.uco.backendvictus.infrastructure.secondary.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.edu.uco.backendvictus.domain.model.Departamento;
import co.edu.uco.backendvictus.domain.port.DepartamentoRepository;
import co.edu.uco.backendvictus.domain.port.PaisRepository;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.DepartamentoEntity;
import co.edu.uco.backendvictus.infrastructure.secondary.mapper.DepartamentoEntityMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class DepartamentoRepositoryAdapter implements DepartamentoRepository {

    private final DepartamentoR2dbcRepository repository;
    private final DepartamentoEntityMapper mapper;
    private final PaisRepository paisRepository;

    public DepartamentoRepositoryAdapter(final DepartamentoR2dbcRepository repository,
            final DepartamentoEntityMapper mapper, final PaisRepository paisRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.paisRepository = paisRepository;
    }

    @Override
    public Mono<Departamento> save(final Departamento departamento) {
        return repository.save(mapper.toEntity(departamento)).flatMap(this::toDomain);
    }

    @Override
    public Mono<Departamento> findById(final UUID id) {
        return repository.findById(id).flatMap(this::toDomain);
    }

    @Override
    public Flux<Departamento> findAll() {
        return repository.findAll().flatMap(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(final UUID id) {
        return repository.deleteById(id);
    }

    private Mono<Departamento> toDomain(final DepartamentoEntity entity) {
        return paisRepository.findById(entity.getPaisId())
                .map(pais -> mapper.toDomain(entity, pais));
    }
}
