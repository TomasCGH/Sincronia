package co.edu.uco.backendvictus.application.usecase.departamento;

import java.util.UUID;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.port.DepartamentoEventoPublisher;
import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoResponse;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.port.DepartamentoRepository;

@Service
public class DeleteDepartamentoUseCase {

    private final DepartamentoRepository departamentoRepository;
    private final DepartamentoEventoPublisher eventoPublisher;

    public DeleteDepartamentoUseCase(final DepartamentoRepository departamentoRepository,
                                     final DepartamentoEventoPublisher eventoPublisher) {
        this.departamentoRepository = departamentoRepository;
        this.eventoPublisher = eventoPublisher;
    }

    public Mono<Void> execute(final UUID id) {
        return departamentoRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApplicationException("Departamento no encontrado")))
                .flatMap(dep -> {
                    DepartamentoResponse payload = new DepartamentoResponse(dep.getId(), dep.getPais().getId(), dep.getNombre());
                    Mono<Void> publish = eventoPublisher != null
                            ? eventoPublisher.publish(new DepartamentoEventoPublisher.Evento(DepartamentoEventoPublisher.TipoEvento.DELETED, payload))
                            : Mono.empty();
                    return publish.then(departamentoRepository.deleteById(id));
                });
    }
}
