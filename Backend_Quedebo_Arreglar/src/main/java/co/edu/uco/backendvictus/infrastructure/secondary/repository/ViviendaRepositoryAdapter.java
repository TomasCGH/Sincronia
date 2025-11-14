package co.edu.uco.backendvictus.infrastructure.secondary.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.edu.uco.backendvictus.application.port.out.conjunto.ConjuntoRepositoryPort;
import co.edu.uco.backendvictus.domain.model.Vivienda;
import co.edu.uco.backendvictus.domain.model.ViviendaEstado;
import co.edu.uco.backendvictus.domain.model.ViviendaTipo;
import co.edu.uco.backendvictus.domain.model.conjunto.ConjuntoResidencial;
import co.edu.uco.backendvictus.domain.port.ViviendaRepository;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.ViviendaEntity;
import co.edu.uco.backendvictus.infrastructure.secondary.mapper.ViviendaEntityMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ViviendaRepositoryAdapter implements ViviendaRepository {

    private final ViviendaR2dbcRepository repository;
    private final ViviendaEntityMapper mapper;
    private final ConjuntoRepositoryPort conjuntoRepository;

    public ViviendaRepositoryAdapter(final ViviendaR2dbcRepository repository, final ViviendaEntityMapper mapper,
            final ConjuntoRepositoryPort conjuntoRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.conjuntoRepository = conjuntoRepository;
    }

    @Override
    public Mono<Vivienda> save(final Vivienda vivienda) {
        return repository.save(mapper.toEntity(vivienda)).flatMap(this::toDomain);
    }

    @Override
    public Mono<Vivienda> findById(final UUID id) {
        return repository.findById(id).flatMap(this::toDomain);
    }

    @Override
    public Flux<Vivienda> findByFilters(final UUID conjuntoId, final ViviendaEstado estado, final ViviendaTipo tipo,
            final String numeroLike, final int page, final int size) {
        return repository.findAll()
                .flatMap(this::toDomain)
                .filter(vivienda -> conjuntoId == null || vivienda.getConjunto().getId().equals(conjuntoId))
                .filter(vivienda -> estado == null || vivienda.getEstado() == estado)
                .filter(vivienda -> tipo == null || vivienda.getTipo() == tipo)
                .filter(vivienda -> numeroLike == null
                        || vivienda.getNumero().toLowerCase().contains(numeroLike.toLowerCase()))
                .skip((long) page * size)
                .take(size);
    }

    @Override
    public Mono<Long> countByFilters(final UUID conjuntoId, final ViviendaEstado estado, final ViviendaTipo tipo,
            final String numeroLike) {
        return repository.findAll()
                .flatMap(this::toDomain)
                .filter(vivienda -> conjuntoId == null || vivienda.getConjunto().getId().equals(conjuntoId))
                .filter(vivienda -> estado == null || vivienda.getEstado() == estado)
                .filter(vivienda -> tipo == null || vivienda.getTipo() == tipo)
                .filter(vivienda -> numeroLike == null
                        || vivienda.getNumero().toLowerCase().contains(numeroLike.toLowerCase()))
                .count();
    }

    @Override
    public Mono<Vivienda> findByConjuntoAndNumero(final UUID conjuntoId, final String numero) {
        return repository.findByConjuntoIdAndNumero(conjuntoId, numero).flatMap(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(final UUID id) {
        return repository.deleteById(id);
    }

    private Mono<Vivienda> toDomain(final ViviendaEntity entity) {
        final Mono<ConjuntoResidencial> conjuntoMono = conjuntoRepository.findById(entity.getConjuntoId());
        return conjuntoMono.map(conjunto -> mapper.toDomain(entity, conjunto));
    }
}
