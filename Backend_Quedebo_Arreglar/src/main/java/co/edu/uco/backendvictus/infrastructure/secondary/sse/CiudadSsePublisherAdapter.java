package co.edu.uco.backendvictus.infrastructure.secondary.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import co.edu.uco.backendvictus.application.port.CiudadEventoPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
public class CiudadSsePublisherAdapter implements CiudadEventoPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(CiudadSsePublisherAdapter.class);
    private final Sinks.Many<Evento> sink = Sinks.many().multicast().directBestEffort();

    @Override
    public Mono<Void> publish(final Evento evento) {
        Sinks.EmitResult result = sink.tryEmitNext(evento);
        if (result.isFailure()) {
            LOGGER.warn("[SSE Ciudades] fallo emitiendo evento: {} - {}", evento.tipo(), result);
        } else {
            LOGGER.info("[SSE Ciudades] evento emitido: {}", evento.tipo());
        }
        return Mono.empty();
    }

    @Override
    public Flux<Evento> stream() { return sink.asFlux(); }
}
