package co.edu.uco.backendvictus.application.usecase.conjunto;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoEvento;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoResponse;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoUpdateRequest;
import co.edu.uco.backendvictus.application.mapper.ConjuntoApplicationMapper;
import co.edu.uco.backendvictus.application.port.out.conjunto.ConjuntoEventoPublisher;
import co.edu.uco.backendvictus.application.port.out.conjunto.ConjuntoRepositoryPort;
import co.edu.uco.backendvictus.application.usecase.UseCase;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.model.Administrador;
import co.edu.uco.backendvictus.domain.model.Ciudad;
import co.edu.uco.backendvictus.domain.model.conjunto.ConjuntoResidencial;
import co.edu.uco.backendvictus.domain.port.AdministradorRepository;
import co.edu.uco.backendvictus.domain.port.CiudadRepository;

@Service
public class UpdateConjuntoUseCase implements UseCase<ConjuntoUpdateRequest, ConjuntoResponse> {

    private final ConjuntoRepositoryPort conjuntoRepository;
    private final CiudadRepository ciudadRepository;
    private final AdministradorRepository administradorRepository;
    private final ConjuntoApplicationMapper mapper;
    private final ConjuntoEventoPublisher eventoPublisher;

    public UpdateConjuntoUseCase(final ConjuntoRepositoryPort conjuntoRepository,
            final CiudadRepository ciudadRepository, final AdministradorRepository administradorRepository,
            final ConjuntoApplicationMapper mapper, final ConjuntoEventoPublisher eventoPublisher) {
        this.conjuntoRepository = conjuntoRepository;
        this.ciudadRepository = ciudadRepository;
        this.administradorRepository = administradorRepository;
        this.mapper = mapper;
        this.eventoPublisher = eventoPublisher;
    }

    @Override
    public Mono<ConjuntoResponse> execute(final ConjuntoUpdateRequest request) {
        return conjuntoRepository.findById(request.id())
                .switchIfEmpty(Mono.error(new ApplicationException("Conjunto residencial no encontrado")))
                .flatMap(existente -> Mono.zip(
                        ciudadRepository.findById(request.ciudadId())
                                .switchIfEmpty(Mono.error(new ApplicationException("Ciudad no encontrada"))),
                        administradorRepository.findById(request.administradorId())
                                .switchIfEmpty(Mono.error(new ApplicationException("Administrador no encontrado")))
                ).map(tuple -> existente.update(request.nombre(), request.direccion(), tuple.getT1(), tuple.getT2(),
                        request.telefono())))
                .flatMap(conjuntoRepository::save)
                .map(mapper::toResponse)
                .flatMap(resp -> eventoPublisher.publish(new ConjuntoEvento("UPDATED", resp)).thenReturn(resp));
    }
}
