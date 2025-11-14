package co.edu.uco.backendvictus.infrastructure.secondary.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import co.edu.uco.backendvictus.application.port.DepartamentoEventoPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
public class DepartamentoSsePublisherAdapter implements DepartamentoEventoPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartamentoSsePublisherAdapter.class);
    private final Sinks.Many<Evento> sink = Sinks.many().multicast().directBestEffort();

    @Override
    public Mono<Void> publish(final Evento evento) {
        Sinks.EmitResult result = sink.tryEmitNext(evento);
        if (result.isFailure()) {
            LOGGER.warn("[SSE Departamentos] fallo emitiendo evento: {} - {}", evento.tipo(), result);
        } else {
            LOGGER.info("[SSE Departamentos] evento emitido: {}", evento.tipo());
        }
        return Mono.empty();
    }

    @Override
    public Flux<Evento> stream() { return sink.asFlux(); }
}
