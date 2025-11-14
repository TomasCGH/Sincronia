package co.edu.uco.backendvictus.infrastructure.primary.controller;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoResponse;
import co.edu.uco.backendvictus.application.port.DepartamentoEventoPublisher;
import co.edu.uco.backendvictus.application.usecase.departamento.ListDepartamentoUseCase;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/departamentos")
public class DepartamentoStreamController {

    private final ListDepartamentoUseCase listDepartamentoUseCase;
    private final DepartamentoEventoPublisher publisher;

    public DepartamentoStreamController(final ListDepartamentoUseCase listDepartamentoUseCase,
                                        final DepartamentoEventoPublisher publisher) {
        this.listDepartamentoUseCase = listDepartamentoUseCase;
        this.publisher = publisher;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<DepartamentoResponse> listarDepartamentos() {
        return listDepartamentoUseCase.execute();
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<DepartamentoEventoPublisher.Evento>> streamDepartamentos() {
        return publisher.stream()
                .map(ev -> ServerSentEvent.<DepartamentoEventoPublisher.Evento>builder()
                        .event(ev.tipo().name())
                        .data(ev)
                        .build());
    }
}
