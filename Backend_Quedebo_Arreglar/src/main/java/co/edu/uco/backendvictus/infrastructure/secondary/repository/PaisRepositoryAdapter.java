package co.edu.uco.backendvictus.infrastructure.secondary.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.edu.uco.backendvictus.domain.model.Pais;
import co.edu.uco.backendvictus.domain.port.PaisRepository;
import co.edu.uco.backendvictus.infrastructure.secondary.mapper.PaisEntityMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class PaisRepositoryAdapter implements PaisRepository {

    private final PaisR2dbcRepository repository;
    private final PaisEntityMapper mapper;

    public PaisRepositoryAdapter(final PaisR2dbcRepository repository, final PaisEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Pais> save(final Pais pais) {
        return repository.save(mapper.toEntity(pais)).map(mapper::toDomain);
    }

    @Override
    public Mono<Pais> findById(final UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Flux<Pais> findAll() {
        return repository.findAll().map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(final UUID id) {
        return repository.deleteById(id);
    }
}
