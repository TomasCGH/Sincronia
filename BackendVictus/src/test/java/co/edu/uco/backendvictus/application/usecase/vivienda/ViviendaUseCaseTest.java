package co.edu.uco.backendvictus.application.usecase.vivienda;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaChangeEstadoRequest;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaCreateRequest;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaFilterRequest;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaUpdateRequest;
import co.edu.uco.backendvictus.application.mapper.ViviendaApplicationMapper;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.model.conjunto.ConjuntoResidencial;
import co.edu.uco.backendvictus.domain.model.Vivienda;
import co.edu.uco.backendvictus.domain.model.ViviendaEstado;
import co.edu.uco.backendvictus.domain.model.ViviendaTipo;
import co.edu.uco.backendvictus.application.port.out.conjunto.ConjuntoRepositoryPort;
import co.edu.uco.backendvictus.domain.port.ViviendaRepository;
import co.edu.uco.backendvictus.seeds.ViviendaFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ViviendaUseCaseTest {

    private ViviendaRepository viviendaRepository;
    private ConjuntoRepositoryPort conjuntoRepository;
    private ViviendaApplicationMapper mapper;
    private ConjuntoResidencial conjunto;

    @BeforeEach
    void setUp() {
        conjunto = ViviendaFactory.buildConjunto();
        viviendaRepository = new InMemoryViviendaRepository();
        conjuntoRepository = new InMemoryConjuntoRepository(conjunto);
        mapper = Mappers.getMapper(ViviendaApplicationMapper.class);
    }

    @Test
    void shouldCreateAndListViviendasWithFilters() {
        final CreateViviendaUseCase createUseCase = new CreateViviendaUseCase(viviendaRepository,
                conjuntoRepository, mapper);
        final ListViviendaUseCase listUseCase = new ListViviendaUseCase(viviendaRepository, mapper);

        final ViviendaCreateRequest request = new ViviendaCreateRequest(conjunto.getId(), "101", "Apartamento",
                "Disponible");

        StepVerifier.create(createUseCase.execute(request))
                .assertNext(response -> {
                    assertEquals("101", response.numero());
                    assertEquals("Apartamento", response.tipo());
                })
                .verifyComplete();

        final ViviendaFilterRequest filter = new ViviendaFilterRequest(conjunto.getId(), "Disponible", null, null, 0,
                10);

        StepVerifier.create(listUseCase.execute(filter))
                .assertNext(page -> {
                    assertEquals(1, page.total());
                    assertEquals(1, page.items().size());
                    assertEquals("101", page.items().get(0).numero());
                })
                .verifyComplete();
    }

    @Test
    void shouldFailWhenNumeroAlreadyExists() {
        final CreateViviendaUseCase createUseCase = new CreateViviendaUseCase(viviendaRepository,
                conjuntoRepository, mapper);

        final ViviendaCreateRequest request = new ViviendaCreateRequest(conjunto.getId(), "101", "Apartamento",
                "Disponible");

        StepVerifier.create(createUseCase.execute(request))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(createUseCase.execute(request))
                .expectError(ApplicationException.class)
                .verify();
    }

    @Test
    void shouldUpdateAndChangeEstado() {
        final CreateViviendaUseCase createUseCase = new CreateViviendaUseCase(viviendaRepository,
                conjuntoRepository, mapper);
        final UpdateViviendaUseCase updateUseCase = new UpdateViviendaUseCase(viviendaRepository,
                conjuntoRepository, mapper);
        final ChangeViviendaEstadoUseCase changeEstadoUseCase = new ChangeViviendaEstadoUseCase(viviendaRepository,
                mapper);

        final ViviendaCreateRequest request = new ViviendaCreateRequest(conjunto.getId(), "201", "Casa",
                "Disponible");

        final AtomicReference<UUID> viviendaId = new AtomicReference<>();

        StepVerifier.create(createUseCase.execute(request))
                .assertNext(response -> {
                    viviendaId.set(response.id());
                    assertEquals("201", response.numero());
                })
                .verifyComplete();

        final ViviendaUpdateRequest updateRequest = new ViviendaUpdateRequest(viviendaId.get(), conjunto.getId(),
                "202", "Dúplex", "Ocupada");

        StepVerifier.create(updateUseCase.execute(updateRequest))
                .assertNext(updated -> {
                    assertEquals("202", updated.numero());
                    assertEquals("Dúplex", updated.tipo());
                    assertEquals("Ocupada", updated.estado());
                })
                .verifyComplete();

        final ViviendaChangeEstadoRequest changeEstadoRequest = new ViviendaChangeEstadoRequest(viviendaId.get(),
                "En mantenimiento");

        StepVerifier.create(changeEstadoUseCase.execute(changeEstadoRequest))
                .assertNext(updated -> assertEquals("En mantenimiento", updated.estado()))
                .verifyComplete();
    }

    private static final class InMemoryConjuntoRepository implements ConjuntoRepositoryPort {

        private final Map<UUID, ConjuntoResidencial> store = new ConcurrentHashMap<>();

        private InMemoryConjuntoRepository(final ConjuntoResidencial conjunto) {
            store.put(conjunto.getId(), conjunto);
        }

        @Override
        public Mono<ConjuntoResidencial> save(final ConjuntoResidencial conjuntoResidencial) {
            store.put(conjuntoResidencial.getId(), conjuntoResidencial);
            return Mono.just(conjuntoResidencial);
        }

        @Override
        public Mono<ConjuntoResidencial> findById(final UUID id) {
            return Mono.justOrEmpty(store.get(id));
        }

        @Override
        public Flux<ConjuntoResidencial> findAll() {
            return Flux.fromIterable(store.values());
        }

        @Override
        public Mono<Void> deleteById(final UUID id) {
            store.remove(id);
            return Mono.empty();
        }

        @Override
        public Mono<ConjuntoResidencial> findByCiudadAndNombre(final java.util.UUID ciudadId, final String nombre) {
            return Flux.fromIterable(store.values())
                    .filter(c -> c.getCiudad() != null && c.getCiudad().getId().equals(ciudadId) && c.getNombre().equals(nombre))
                    .next();
        }

        @Override
        public Flux<ConjuntoResidencial> findAllByTelefono(final String telefono) {
            return Flux.fromIterable(store.values())
                    .filter(c -> c.getTelefono() != null && c.getTelefono().equals(telefono));
        }

        @Override
        public Flux<ConjuntoResidencial> findAllWithNames() {
            return Flux.fromIterable(store.values());
        }

        @Override
        public Flux<ConjuntoResidencial> findAllWithNamesPaged(final int page, final int size) {
            return findAllWithNames()
                    .skip((long) Math.max(page, 0) * Math.max(1, size))
                    .take(Math.max(1, size));
        }

        @Override
        public Mono<Long> countAll() {
            return Mono.just((long) store.size());
        }

        @Override
        public Flux<ConjuntoResidencial> findByDepartamentoId(final UUID departamentoId) {
            return Flux.fromIterable(store.values())
                    .filter(c -> c.getCiudad() != null && c.getCiudad().getDepartamento() != null
                            && c.getCiudad().getDepartamento().getId().equals(departamentoId));
        }

        @Override
        public Flux<ConjuntoResidencial> findByCiudadId(final UUID ciudadId) {
            return Flux.fromIterable(store.values())
                    .filter(c -> c.getCiudad() != null && c.getCiudad().getId().equals(ciudadId));
        }

        @Override
        public Flux<ConjuntoResidencial> findByDepartamentoIdAndCiudadId(final UUID departamentoId, final UUID ciudadId) {
            return Flux.fromIterable(store.values())
                    .filter(c -> c.getCiudad() != null && c.getCiudad().getDepartamento() != null
                            && c.getCiudad().getDepartamento().getId().equals(departamentoId)
                            && c.getCiudad().getId().equals(ciudadId));
        }

        @Override
        public Flux<ConjuntoResidencial> findByNombre(final String nombre) {
            return Flux.fromIterable(store.values())
                    .filter(c -> c.getNombre().toLowerCase().contains(nombre.toLowerCase()));
        }
    }

    private final class InMemoryViviendaRepository implements ViviendaRepository {

        private final Map<UUID, Vivienda> store = new ConcurrentHashMap<>();

        @Override
        public Mono<Vivienda> save(final Vivienda vivienda) {
            store.put(vivienda.getId(), vivienda);
            return Mono.just(vivienda);
        }

        @Override
        public Mono<Vivienda> findById(final UUID id) {
            return Mono.justOrEmpty(store.get(id));
        }

        @Override
        public Flux<Vivienda> findByFilters(final UUID conjuntoId, final ViviendaEstado estado, final ViviendaTipo tipo,
                final String numeroLike, final int page, final int size) {
            return Flux.fromIterable(store.values())
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
            return findByFilters(conjuntoId, estado, tipo, numeroLike, 0, Integer.MAX_VALUE).count();
        }

        @Override
        public Mono<Vivienda> findByConjuntoAndNumero(final UUID conjuntoId, final String numero) {
            return Flux.fromIterable(store.values())
                    .filter(vivienda -> vivienda.getConjunto().getId().equals(conjuntoId)
                            && vivienda.getNumero().equals(numero))
                    .next();
        }

        @Override
        public Mono<Void> deleteById(final UUID id) {
            store.remove(id);
            return Mono.empty();
        }
    }
}
