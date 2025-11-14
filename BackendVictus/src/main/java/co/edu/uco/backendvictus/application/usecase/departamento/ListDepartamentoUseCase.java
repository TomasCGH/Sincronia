package co.edu.uco.backendvictus.application.usecase.departamento;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoResponse;
import co.edu.uco.backendvictus.application.mapper.DepartamentoApplicationMapper;
import co.edu.uco.backendvictus.domain.port.DepartamentoRepository;

@Service
public class ListDepartamentoUseCase {

    private final DepartamentoRepository departamentoRepository;
    private final DepartamentoApplicationMapper mapper;

    public ListDepartamentoUseCase(final DepartamentoRepository departamentoRepository,
            final DepartamentoApplicationMapper mapper) {
        this.departamentoRepository = departamentoRepository;
        this.mapper = mapper;
    }

    public Flux<DepartamentoResponse> execute() {
        return departamentoRepository.findAll().map(mapper::toResponse);
    }
}
