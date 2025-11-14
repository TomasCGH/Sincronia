package co.edu.uco.backendvictus.infrastructure.primary.controller;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoResponse;
import co.edu.uco.backendvictus.application.service.DepartamentoService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/departamentos")
public class DepartamentoStreamController {

    private final DepartamentoService departamentoService;

    public DepartamentoStreamController(final DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<DepartamentoResponse> listarDepartamentos() {
        return departamentoService.listarDepartamentos();
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<DepartamentoResponse>> streamDepartamentos() {
        return departamentoService.streamDepartamentos()
                .map(dep -> ServerSentEvent.<DepartamentoResponse>builder()
                        .event("departamento")
                        .data(dep)
                        .build());
    }
}
