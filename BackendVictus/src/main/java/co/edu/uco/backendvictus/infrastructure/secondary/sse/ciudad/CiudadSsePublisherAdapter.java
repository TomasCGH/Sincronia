package co.edu.uco.backendvictus.infrastructure.secondary.sse.ciudad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import co.edu.uco.backendvictus.application.dto.ciudad.CiudadEvento;
import co.edu.uco.backendvictus.application.port.out.ciudad.CiudadEventoPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
public class CiudadSsePublisherAdapter implements CiudadEventoPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(CiudadSsePublisherAdapter.class);

    private final Sinks.Many<CiudadEvento> sink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Mono<Void> publish(final CiudadEvento evento) {
        final Sinks.EmitResult result = sink.tryEmitNext(evento);
        if (result.isFailure()) {
            LOGGER.warn("[SSE Ciudades] fallo emitiendo evento {} -> {}", evento.tipo(), result);
        } else {
            LOGGER.info("[SSE Ciudades] evento emitido: {}", evento.tipo());
        }
        return Mono.empty();
    }

    @Override
    public Flux<CiudadEvento> stream() {
        return sink.asFlux();
    }
}
