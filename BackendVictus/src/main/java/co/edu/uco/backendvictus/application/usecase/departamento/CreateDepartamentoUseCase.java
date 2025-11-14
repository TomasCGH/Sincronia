package co.edu.uco.backendvictus.application.usecase.departamento;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoCreateRequest;
import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoResponse;
import co.edu.uco.backendvictus.application.mapper.DepartamentoApplicationMapper;
import co.edu.uco.backendvictus.application.usecase.UseCase;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.crosscutting.helpers.UuidGenerator;
import co.edu.uco.backendvictus.domain.model.Departamento;
import co.edu.uco.backendvictus.domain.model.Pais;
import co.edu.uco.backendvictus.domain.port.DepartamentoRepository;
import co.edu.uco.backendvictus.domain.port.PaisRepository;

@Service
public class CreateDepartamentoUseCase implements UseCase<DepartamentoCreateRequest, DepartamentoResponse> {

    private final DepartamentoRepository departamentoRepository;
    private final PaisRepository paisRepository;
    private final DepartamentoApplicationMapper mapper;

    public CreateDepartamentoUseCase(final DepartamentoRepository departamentoRepository,
            final PaisRepository paisRepository, final DepartamentoApplicationMapper mapper) {
        this.departamentoRepository = departamentoRepository;
        this.paisRepository = paisRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<DepartamentoResponse> execute(final DepartamentoCreateRequest request) {
        return paisRepository.findById(request.paisId())
                .switchIfEmpty(Mono.error(new ApplicationException("Pais no encontrado")))
                .map(pais -> mapper.toDomain(null, request, pais))
                .flatMap(departamentoRepository::save)
                .map(mapper::toResponse);
    }
}
