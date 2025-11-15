package co.edu.uco.backendvictus.application.usecase.departamento;

import java.util.UUID;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.mapper.DepartamentoApplicationMapper;
import co.edu.uco.backendvictus.application.port.out.departamento.DepartamentoEventoPublisher;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.port.DepartamentoRepository;

@Service
public class DeleteDepartamentoUseCase {

    private final DepartamentoRepository departamentoRepository;
    private final DepartamentoApplicationMapper mapper;
    private final DepartamentoEventoPublisher eventoPublisher;

    public DeleteDepartamentoUseCase(final DepartamentoRepository departamentoRepository,
            final DepartamentoApplicationMapper mapper,
            final DepartamentoEventoPublisher eventoPublisher) {
        this.departamentoRepository = departamentoRepository;
        this.mapper = mapper;
        this.eventoPublisher = eventoPublisher;
    }

    public Mono<Void> execute(final UUID id) {
        return departamentoRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApplicationException("Departamento no encontrado")))
                .flatMap(departamento -> departamentoRepository.deleteById(id)
                        .thenReturn(mapper.toResponse(departamento)))
                .flatMap(eventoPublisher::emitDeleted);
    }
}
