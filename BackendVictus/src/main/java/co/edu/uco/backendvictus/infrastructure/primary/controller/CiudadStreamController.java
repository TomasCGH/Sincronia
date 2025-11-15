package co.edu.uco.backendvictus.infrastructure.primary.controller;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.backendvictus.application.dto.ciudad.CiudadEvento;
import co.edu.uco.backendvictus.application.dto.ciudad.CiudadPublicResponse;
import co.edu.uco.backendvictus.application.dto.ciudad.CiudadResponse;
import co.edu.uco.backendvictus.application.port.out.ciudad.CiudadEventoPublisher;
import co.edu.uco.backendvictus.application.service.CiudadService;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

@RestController
@RequestMapping("/api/v1/ciudades")
public class CiudadStreamController {

    private static final Duration HEARTBEAT_INTERVAL = Duration.ofSeconds(15);

    private final CiudadService ciudadService;
    private final CiudadEventoPublisher eventoPublisher;

    public CiudadStreamController(final CiudadService ciudadService,
            final CiudadEventoPublisher eventoPublisher) {
        this.ciudadService = ciudadService;
        this.eventoPublisher = eventoPublisher;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<CiudadPublicResponse> listarCiudades() {
        return ciudadService.listarCiudades()
                .map(this::toPublicResponse);
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<ServerSentEvent<Map<String, Object>>>> streamCiudades() {
        final HttpHeaders headers = buildSseHeaders();

        Flux<ServerSentEvent<Map<String, Object>>> eventos = eventoPublisher.stream()
                .map(evento -> ServerSentEvent.<Map<String, Object>>builder()
                        .event(evento.getTipo())
                        .data(buildPublicEvent(evento))
                        .build());

        Flux<ServerSentEvent<Map<String, Object>>> heartbeat = Flux.interval(HEARTBEAT_INTERVAL)
                .map(sequence -> ServerSentEvent.<Map<String, Object>>builder()
                        .event("heartbeat")
                        .comment("keep-alive")
                        .build());

        Flux<ServerSentEvent<Map<String, Object>>> resilientStream = Flux.merge(eventos, heartbeat)
                .onBackpressureBuffer()
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(2))
                        .maxBackoff(Duration.ofSeconds(30))
                        .jitter(0.2));

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(resilientStream);
    }

    private static HttpHeaders buildSseHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noStore().mustRevalidate().getHeaderValue());
        headers.setPragma("no-cache");
        headers.add("X-Accel-Buffering", "no");
        headers.add("Connection", "keep-alive");
        return headers;
    }

    // --- helpers locales ---
    private CiudadPublicResponse toPublicResponse(final CiudadResponse r) {
        if (r == null) {
            return null;
        }
        return new CiudadPublicResponse(r.ciudadId(), r.ciudadNombre(), r.departamentoId());
    }

    private Map<String, Object> buildPublicEvent(final CiudadEvento evento) {
        final CiudadResponse payload = evento.getPayload();
        final CiudadPublicResponse publicPayload = toPublicResponse(payload);
        return Map.of(
                "tipo", evento.getTipo(),
                "payload", publicPayload
        );
    }
}
