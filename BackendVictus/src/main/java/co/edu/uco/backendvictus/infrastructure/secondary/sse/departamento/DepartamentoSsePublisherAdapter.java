package co.edu.uco.backendvictus.infrastructure.secondary.sse.departamento;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoEvento;
import co.edu.uco.backendvictus.application.port.out.departamento.DepartamentoEventoPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
public class DepartamentoSsePublisherAdapter implements DepartamentoEventoPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartamentoSsePublisherAdapter.class);

    private final Sinks.Many<DepartamentoEvento> sink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Mono<Void> publish(final DepartamentoEvento evento) {
        final Sinks.EmitResult result = sink.tryEmitNext(evento);
        if (result.isFailure()) {
            LOGGER.warn("[SSE Departamentos] fallo emitiendo evento {} -> {}", evento.getTipo(), result);
        } else {
            LOGGER.info("[SSE Departamentos] evento emitido: {}", evento.getTipo());
        }
        return Mono.empty();
    }

    @Override
    public Flux<DepartamentoEvento> stream() {
        return sink.asFlux();
    }
}
