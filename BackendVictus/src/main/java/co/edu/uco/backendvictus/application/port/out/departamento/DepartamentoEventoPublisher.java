package co.edu.uco.backendvictus.application.port.out.departamento;

import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoEvento;
import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoResponse;
import co.edu.uco.backendvictus.application.dto.evento.TipoEvento;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DepartamentoEventoPublisher {

    Mono<Void> publish(DepartamentoEvento evento);

    Flux<DepartamentoEvento> stream();

    default Mono<Void> emitCreated(final DepartamentoResponse payload) {
        return publish(DepartamentoEvento.of(TipoEvento.CREATED, payload));
    }

    default Mono<Void> emitUpdated(final DepartamentoResponse payload) {
        return publish(DepartamentoEvento.of(TipoEvento.UPDATED, payload));
    }

    default Mono<Void> emitDeleted(final DepartamentoResponse payload) {
        return publish(DepartamentoEvento.of(TipoEvento.DELETED, payload));
    }
}
