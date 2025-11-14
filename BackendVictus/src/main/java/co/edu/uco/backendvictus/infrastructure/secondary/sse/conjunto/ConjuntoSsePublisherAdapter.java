package co.edu.uco.backendvictus.infrastructure.secondary.sse.conjunto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoEvento;
import co.edu.uco.backendvictus.application.port.out.conjunto.ConjuntoEventoPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Primary
@Component
public class ConjuntoSsePublisherAdapter implements ConjuntoEventoPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConjuntoSsePublisherAdapter.class);
    private final Sinks.Many<ConjuntoEvento> sink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Mono<Void> publish(final ConjuntoEvento evento) {
        Sinks.EmitResult result = sink.tryEmitNext(evento);
        if (result.isFailure()) {
            LOGGER.warn("Fallo emitiendo evento SSE de conjunto: {} - resultado: {}", evento, result);
        }
        return Mono.empty();
    }

    @Override
    public Flux<ConjuntoEvento> stream() {
        return sink.asFlux();
    }
}

