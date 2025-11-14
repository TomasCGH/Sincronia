package co.edu.uco.backendvictus.application.usecase.pais;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

import co.edu.uco.backendvictus.application.dto.pais.PaisResponse;
import co.edu.uco.backendvictus.application.mapper.PaisApplicationMapper;
import co.edu.uco.backendvictus.domain.port.PaisRepository;

@Service
public class ListPaisUseCase {

    private final PaisRepository repository;
    private final PaisApplicationMapper mapper;

    public ListPaisUseCase(final PaisRepository repository, final PaisApplicationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Flux<PaisResponse> execute() {
        return repository.findAll().map(mapper::toResponse);
    }
}
