package co.edu.uco.backendvictus.infrastructure.secondary.sse.administrador;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import co.edu.uco.backendvictus.application.dto.administrador.AdministradorEvento;
import co.edu.uco.backendvictus.application.port.out.administrador.AdministradorEventoPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Primary
@Component
public class AdministradorSsePublisherAdapter implements AdministradorEventoPublisher {

    private final Sinks.Many<AdministradorEvento> sink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Mono<Void> publish(final AdministradorEvento evento) {
        sink.tryEmitNext(evento);
        return Mono.empty();
    }

    @Override
    public Flux<AdministradorEvento> stream() {
        return sink.asFlux();
    }
}

