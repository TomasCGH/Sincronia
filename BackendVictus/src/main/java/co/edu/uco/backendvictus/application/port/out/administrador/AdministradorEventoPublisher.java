package co.edu.uco.backendvictus.application.port.out.administrador;

import co.edu.uco.backendvictus.application.dto.administrador.AdministradorCatalogResponse;
import co.edu.uco.backendvictus.application.dto.administrador.AdministradorEvento;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AdministradorEventoPublisher {

    Mono<Void> publish(AdministradorEvento evento);

    Flux<AdministradorEvento> stream();

    default Mono<Void> emitCreated(final AdministradorCatalogResponse payload) {
        return publish(new AdministradorEvento("CREATED", payload));
    }

    default Mono<Void> emitUpdated(final AdministradorCatalogResponse payload) {
        return publish(new AdministradorEvento("UPDATED", payload));
    }

    default Mono<Void> emitDeleted(final AdministradorCatalogResponse payload) {
        return publish(new AdministradorEvento("DELETED", payload));
    }
}

