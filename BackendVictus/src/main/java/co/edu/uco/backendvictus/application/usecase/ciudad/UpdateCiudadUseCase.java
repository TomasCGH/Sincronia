package co.edu.uco.backendvictus.application.usecase.ciudad;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.dto.ciudad.CiudadResponse;
import co.edu.uco.backendvictus.application.dto.ciudad.CiudadUpdateRequest;
import co.edu.uco.backendvictus.application.mapper.CiudadApplicationMapper;
import co.edu.uco.backendvictus.application.usecase.UseCase;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.model.Ciudad;
import co.edu.uco.backendvictus.domain.model.Departamento;
import co.edu.uco.backendvictus.domain.port.CiudadRepository;
import co.edu.uco.backendvictus.domain.port.DepartamentoRepository;

@Service
public class UpdateCiudadUseCase implements UseCase<CiudadUpdateRequest, CiudadResponse> {

    private final CiudadRepository ciudadRepository;
    private final DepartamentoRepository departamentoRepository;
    private final CiudadApplicationMapper mapper;

    public UpdateCiudadUseCase(final CiudadRepository ciudadRepository,
            final DepartamentoRepository departamentoRepository, final CiudadApplicationMapper mapper) {
        this.ciudadRepository = ciudadRepository;
        this.departamentoRepository = departamentoRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<CiudadResponse> execute(final CiudadUpdateRequest request) {
        return ciudadRepository.findById(request.id())
                .switchIfEmpty(Mono.error(new ApplicationException("Ciudad no encontrada")))
                .flatMap(existente -> departamentoRepository.findById(request.departamentoId())
                        .switchIfEmpty(Mono.error(new ApplicationException("Departamento no encontrado")))
                        .map(departamento -> existente.update(request.nombre(), departamento)))
                .flatMap(ciudadRepository::save)
                .map(mapper::toResponse);
    }
}
