package co.edu.uco.backendvictus.infrastructure.secondary.client;

import co.edu.uco.backendvictus.crosscutting.helpers.LoggerHelper;
import org.slf4j.Logger;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class ParameterClient {

    private static final Logger LOGGER = LoggerHelper.getLogger(ParameterClient.class);

    private final WebClient webClient;

    public record ParameterResult(String key, String value, String source) {}

    private record RemoteParameterResponse(String key, String value) {}

    public ParameterClient(final WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ParameterResult> get(final String key) {
        LOGGER.info("ParameterClient → consultando parámetro '{}'", key);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{key}").build(key))
                .exchangeToMono(response -> handleResponse(key, response.statusCode(), response))
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(ex -> {
                    LOGGER.warn("ParameterClient → error consultando '{}' ({}). Respuesta vacía.", key,
                            ex.getClass().getSimpleName());
                    return Mono.empty();
                });
    }

    private Mono<ParameterResult> handleResponse(final String key, final HttpStatusCode status,
            final org.springframework.web.reactive.function.client.ClientResponse response) {
        if (status.is2xxSuccessful()) {
            return response.bodyToMono(RemoteParameterResponse.class)
                    .map(resp -> {
                        final String value = resp != null ? resp.value() : null;
                        LOGGER.info("ParameterService → parámetro \"{}\" = {}", key, value);
                        return new ParameterResult(key, value, "parameter-service");
                    });
        }
        LOGGER.warn("ParameterClient → status {} al consultar '{}'. Respuesta vacía.", status.value(), key);
        return response.bodyToMono(String.class).then(Mono.empty());
    }

    public static ParameterClient fallback() {
        return new ParameterClient(WebClient.builder().baseUrl("http://localhost").build()) {
            @Override
            public Mono<ParameterResult> get(final String key) {
                return Mono.empty();
            }
        };
    }
}
