package co.edu.uco.backendvictus.application.usecase.pais;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.dto.pais.PaisCreateRequest;
import co.edu.uco.backendvictus.application.dto.pais.PaisResponse;
import co.edu.uco.backendvictus.application.mapper.PaisApplicationMapper;
import co.edu.uco.backendvictus.application.usecase.UseCase;
import co.edu.uco.backendvictus.domain.model.Pais;
import co.edu.uco.backendvictus.domain.port.PaisRepository;
import co.edu.uco.backendvictus.crosscutting.helpers.UuidGenerator;

@Service
public class CreatePaisUseCase implements UseCase<PaisCreateRequest, PaisResponse> {

    private final PaisRepository repository;
    private final PaisApplicationMapper mapper;

    public CreatePaisUseCase(final PaisRepository repository, final PaisApplicationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<PaisResponse> execute(final PaisCreateRequest request) {
        return Mono.fromSupplier(() -> mapper.toDomain(null, request))
                .flatMap(repository::save)
                .map(mapper::toResponse);
    }
}
