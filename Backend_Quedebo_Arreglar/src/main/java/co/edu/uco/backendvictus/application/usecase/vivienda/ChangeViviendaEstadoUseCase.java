package co.edu.uco.backendvictus.application.usecase.vivienda;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaChangeEstadoRequest;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaResponse;
import co.edu.uco.backendvictus.application.mapper.ViviendaApplicationMapper;
import co.edu.uco.backendvictus.application.usecase.UseCase;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.port.ViviendaRepository;

@Service
public class ChangeViviendaEstadoUseCase implements UseCase<ViviendaChangeEstadoRequest, ViviendaResponse> {

    private final ViviendaRepository viviendaRepository;
    private final ViviendaApplicationMapper mapper;

    public ChangeViviendaEstadoUseCase(final ViviendaRepository viviendaRepository,
            final ViviendaApplicationMapper mapper) {
        this.viviendaRepository = viviendaRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<ViviendaResponse> execute(final ViviendaChangeEstadoRequest request) {
        return viviendaRepository.findById(request.id())
                .switchIfEmpty(Mono.error(new ApplicationException("Vivienda no encontrada")))
                .map(vivienda -> mapper.applyEstadoChange(vivienda, request))
                .flatMap(viviendaRepository::save)
                .map(mapper::toResponse);
    }
}
