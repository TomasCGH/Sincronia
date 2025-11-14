package co.edu.uco.backendvictus.application.usecase.pais;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import co.edu.uco.backendvictus.application.dto.pais.PaisCreateRequest;
import co.edu.uco.backendvictus.application.dto.pais.PaisUpdateRequest;
import co.edu.uco.backendvictus.application.mapper.PaisApplicationMapper;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.model.Pais;
import co.edu.uco.backendvictus.domain.port.PaisRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class PaisUseCaseTest {

    private PaisRepository repository;
    private PaisApplicationMapper mapper;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPaisRepository();
        mapper = Mappers.getMapper(PaisApplicationMapper.class);
    }

    @Test
    void shouldCreatePais() {
        final CreatePaisUseCase useCase = new CreatePaisUseCase(repository, mapper);
        final PaisCreateRequest request = new PaisCreateRequest("  Colombia  ");

        StepVerifier.create(useCase.execute(request))
                .assertNext(response -> {
                    assertEquals("Colombia", response.nombre());
                    assertNotNull(response.id());
                })
                .verifyComplete();

        StepVerifier.create(repository.findAll().count())
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void shouldUpdateAndDeletePais() {
        final CreatePaisUseCase createUseCase = new CreatePaisUseCase(repository, mapper);
        final UpdatePaisUseCase updateUseCase = new UpdatePaisUseCase(repository, mapper);
        final DeletePaisUseCase deleteUseCase = new DeletePaisUseCase(repository);

        final PaisResponseHolder holder = new PaisResponseHolder();

        StepVerifier.create(createUseCase.execute(new PaisCreateRequest("Peru")))
                .assertNext(response -> {
                    holder.response = response;
                    assertEquals("Peru", response.nombre());
                })
                .verifyComplete();

        final UUID paisId = holder.response.id();

        final PaisUpdateRequest updateRequest = new PaisUpdateRequest(paisId, "Peru Actualizado");

        StepVerifier.create(updateUseCase.execute(updateRequest))
                .assertNext(updated -> {
                    assertEquals("Peru Actualizado", updated.nombre());
                    assertEquals(paisId, updated.id());
                })
                .verifyComplete();

        StepVerifier.create(deleteUseCase.execute(paisId))
                .verifyComplete();

        StepVerifier.create(repository.findById(paisId))
                .verifyComplete();

        StepVerifier.create(deleteUseCase.execute(paisId))
                .expectError(ApplicationException.class)
                .verify();
    }

    private static final class PaisResponseHolder {
        private co.edu.uco.backendvictus.application.dto.pais.PaisResponse response;
    }

    private static final class InMemoryPaisRepository implements PaisRepository {

        private final Map<UUID, Pais> store = new ConcurrentHashMap<>();

        @Override
        public Mono<Pais> save(final Pais pais) {
            store.put(pais.getId(), pais);
            return Mono.just(pais);
        }

        @Override
        public Mono<Pais> findById(final UUID id) {
            return Mono.justOrEmpty(store.get(id));
        }

        @Override
        public Flux<Pais> findAll() {
            return Flux.fromIterable(store.values());
        }

        @Override
        public Mono<Void> deleteById(final UUID id) {
            store.remove(id);
            return Mono.empty();
        }
    }
}
