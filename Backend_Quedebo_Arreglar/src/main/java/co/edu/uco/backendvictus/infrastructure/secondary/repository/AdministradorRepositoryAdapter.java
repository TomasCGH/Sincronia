package co.edu.uco.backendvictus.infrastructure.secondary.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.edu.uco.backendvictus.domain.model.Administrador;
import co.edu.uco.backendvictus.domain.port.AdministradorRepository;
import co.edu.uco.backendvictus.infrastructure.secondary.mapper.AdministradorEntityMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class AdministradorRepositoryAdapter implements AdministradorRepository {

    private final AdministradorR2dbcRepository repository;
    private final AdministradorEntityMapper mapper;

    public AdministradorRepositoryAdapter(final AdministradorR2dbcRepository repository,
            final AdministradorEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Administrador> save(final Administrador administrador) {
        return repository.save(mapper.toEntity(administrador)).map(mapper::toDomain);
    }

    @Override
    public Mono<Administrador> findById(final UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Flux<Administrador> findAll() {
        return repository.findAll().map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(final UUID id) {
        return repository.deleteById(id);
    }
}
