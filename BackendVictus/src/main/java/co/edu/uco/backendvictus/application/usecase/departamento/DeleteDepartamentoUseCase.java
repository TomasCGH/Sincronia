package co.edu.uco.backendvictus.application.usecase.departamento;

import java.util.UUID;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.port.DepartamentoRepository;

@Service
public class DeleteDepartamentoUseCase {

    private final DepartamentoRepository departamentoRepository;

    public DeleteDepartamentoUseCase(final DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    public Mono<Void> execute(final UUID id) {
        return departamentoRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApplicationException("Departamento no encontrado")))
                .then(departamentoRepository.deleteById(id));
    }
}
