package co.edu.uco.backendvictus.application.port;

import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DepartamentoEventoPublisher {
    enum TipoEvento { CREATED, UPDATED, DELETED }
    record Evento(TipoEvento tipo, DepartamentoResponse data) {}
    Mono<Void> publish(Evento evento);
    Flux<Evento> stream();
}
