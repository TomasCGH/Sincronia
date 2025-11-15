package co.edu.uco.backendvictus.application.usecase.ciudad;

import java.util.UUID;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.dto.ciudad.CiudadEvento;
import co.edu.uco.backendvictus.application.dto.evento.TipoEvento;
import co.edu.uco.backendvictus.application.mapper.CiudadApplicationMapper;
import co.edu.uco.backendvictus.application.port.out.ciudad.CiudadEventoPublisher;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.port.CiudadRepository;

@Service
public class DeleteCiudadUseCase {

    private final CiudadRepository ciudadRepository;
    private final CiudadApplicationMapper mapper;
    private final CiudadEventoPublisher eventoPublisher;

    public DeleteCiudadUseCase(final CiudadRepository ciudadRepository,
            final CiudadApplicationMapper mapper,
            final CiudadEventoPublisher eventoPublisher) {
        this.ciudadRepository = ciudadRepository;
        this.mapper = mapper;
        this.eventoPublisher = eventoPublisher;
    }

    public Mono<Void> execute(final UUID id) {
        return ciudadRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApplicationException("Ciudad no encontrada")))
                .flatMap(ciudad -> ciudadRepository.deleteById(id)
                        .thenReturn(mapper.toResponse(ciudad)))
                .flatMap(resp -> eventoPublisher.publish(new CiudadEvento(TipoEvento.DELETED, resp)));
    }
}
