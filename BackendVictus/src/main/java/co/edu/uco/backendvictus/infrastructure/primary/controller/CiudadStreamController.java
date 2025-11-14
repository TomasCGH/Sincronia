package co.edu.uco.backendvictus.infrastructure.primary.controller;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.backendvictus.application.dto.ciudad.CiudadResponse;
import co.edu.uco.backendvictus.application.service.CiudadService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/ciudades")
public class CiudadStreamController {

    private final CiudadService ciudadService;

    public CiudadStreamController(final CiudadService ciudadService) {
        this.ciudadService = ciudadService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<CiudadResponse> listarCiudades() {
        return ciudadService.listarCiudades();
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<CiudadResponse>> streamCiudades() {
        return ciudadService.streamCiudades()
                .map(ciudad -> ServerSentEvent.<CiudadResponse>builder()
                        .event("ciudad")
                        .data(ciudad)
                        .build());
    }
}
