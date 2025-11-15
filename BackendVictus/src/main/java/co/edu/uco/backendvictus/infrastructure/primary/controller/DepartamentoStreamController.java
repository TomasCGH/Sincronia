package co.edu.uco.backendvictus.infrastructure.primary.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoEvento;
import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoResponse;
import co.edu.uco.backendvictus.application.port.out.departamento.DepartamentoEventoPublisher;
import co.edu.uco.backendvictus.application.service.DepartamentoService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/departamentos")
public class DepartamentoStreamController {

    private final DepartamentoService departamentoService;
    private final DepartamentoEventoPublisher eventoPublisher;

    public DepartamentoStreamController(final DepartamentoService departamentoService,
            final DepartamentoEventoPublisher eventoPublisher) {
        this.departamentoService = departamentoService;
        this.eventoPublisher = eventoPublisher;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<DepartamentoResponse> listarDepartamentos() {
        return departamentoService.listarDepartamentos();
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<ServerSentEvent<DepartamentoEvento>>> streamDepartamentos() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl("no-cache");
        headers.add("X-Accel-Buffering", "no");
        headers.add("Connection", "keep-alive");

        Flux<ServerSentEvent<DepartamentoEvento>> body = eventoPublisher.stream()
                .map(evento -> ServerSentEvent.<DepartamentoEvento>builder()
                        .event(evento.tipo().name())
                        .data(evento)
                        .build());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(body);
    }
}
