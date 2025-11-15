package co.edu.uco.backendvictus.application.usecase.ciudad;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.dto.ciudad.CiudadCreateRequest;
import co.edu.uco.backendvictus.application.dto.ciudad.CiudadEvento;
import co.edu.uco.backendvictus.application.dto.ciudad.CiudadResponse;
import co.edu.uco.backendvictus.application.dto.evento.TipoEvento;
import co.edu.uco.backendvictus.application.mapper.CiudadApplicationMapper;
import co.edu.uco.backendvictus.application.usecase.UseCase;
import co.edu.uco.backendvictus.application.port.out.ciudad.CiudadEventoPublisher;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.crosscutting.helpers.UuidGenerator;
import co.edu.uco.backendvictus.domain.model.Ciudad;
import co.edu.uco.backendvictus.domain.model.Departamento;
import co.edu.uco.backendvictus.domain.port.CiudadRepository;
import co.edu.uco.backendvictus.domain.port.DepartamentoRepository;

@Service
public class CreateCiudadUseCase implements UseCase<CiudadCreateRequest, CiudadResponse> {

    private final CiudadRepository ciudadRepository;
    private final DepartamentoRepository departamentoRepository;
    private final CiudadApplicationMapper mapper;
    private final CiudadEventoPublisher eventoPublisher;

    public CreateCiudadUseCase(final CiudadRepository ciudadRepository,
            final DepartamentoRepository departamentoRepository, final CiudadApplicationMapper mapper,
            final CiudadEventoPublisher eventoPublisher) {
        this.ciudadRepository = ciudadRepository;
        this.departamentoRepository = departamentoRepository;
        this.mapper = mapper;
        this.eventoPublisher = eventoPublisher;
    }

    @Override
    public Mono<CiudadResponse> execute(final CiudadCreateRequest request) {
        return departamentoRepository.findById(request.departamentoId())
                .switchIfEmpty(Mono.error(new ApplicationException("Departamento no encontrado")))
                .map(departamento -> mapper.toDomain(null, request, departamento))
                .flatMap(ciudadRepository::save)
                .map(mapper::toResponse)
                .flatMap(resp -> eventoPublisher.publish(new CiudadEvento(TipoEvento.CREATED, resp)).thenReturn(resp));
    }
}
