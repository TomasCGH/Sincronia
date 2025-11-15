package co.edu.uco.backendvictus.application.port.out.departamento;

import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoEvento;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DepartamentoEventoPublisher {

    Mono<Void> publish(DepartamentoEvento evento);

    Flux<DepartamentoEvento> stream();
}
