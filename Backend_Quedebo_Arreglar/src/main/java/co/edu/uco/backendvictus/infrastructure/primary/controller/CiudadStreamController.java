package co.edu.uco.backendvictus.infrastructure.primary.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.backendvictus.application.dto.ciudad.CiudadResponse;
import co.edu.uco.backendvictus.application.port.CiudadEventoPublisher;
import co.edu.uco.backendvictus.application.usecase.ciudad.ListCiudadUseCase;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/ciudades")
public class CiudadStreamController {

    private final ListCiudadUseCase listCiudadUseCase;
    private final CiudadEventoPublisher publisher;

    public CiudadStreamController(final ListCiudadUseCase listCiudadUseCase,
                                  final CiudadEventoPublisher publisher) {
        this.listCiudadUseCase = listCiudadUseCase;
        this.publisher = publisher;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<CiudadResponse> listarCiudades() {
        return listCiudadUseCase.execute();
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<CiudadEventoPublisher.Evento>> streamCiudades() {
        return publisher.stream()
                .map(ev -> ServerSentEvent.<CiudadEventoPublisher.Evento>builder()
                        .event(ev.tipo().name())
                        .data(ev)
                        .build());
    }
}
