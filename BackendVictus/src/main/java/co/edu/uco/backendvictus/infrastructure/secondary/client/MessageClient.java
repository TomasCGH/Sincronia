package co.edu.uco.backendvictus.infrastructure.secondary.client;

import co.edu.uco.backendvictus.crosscutting.helpers.LoggerHelper;
import org.slf4j.Logger;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class MessageClient {

    private static final Logger LOGGER = LoggerHelper.getLogger(MessageClient.class);

    private final WebClient webClient;

    public record MessageResult(String technicalMessage, String clientMessage, String source) {}

    // Estructura flexible: soporta payloads {key,value} ó {key,technicalMessage,clientMessage}
    private record RemoteMessageResponse(String key, String value, String technicalMessage, String clientMessage) {}

    public MessageClient(final WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MessageResult> getMessage(final String key) {
        LOGGER.info("MessageClient → consultando mensaje con key='{}'", key);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{key}").build(key))
                .exchangeToMono(response -> handleResponse(key, response.statusCode(), response))
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(throwable -> {
                    LOGGER.warn("MessageClient → error consultando '{}' ({}). Respuesta vacía.", key,
                            throwable.getClass().getSimpleName());
                    return Mono.empty();
                });
    }

    private MessageResult mapToResultOrDefault(final String key, final RemoteMessageResponse resp) {
        if (resp == null) {
            return null;
        }
        final String technical = nonBlank(resp.technicalMessage()) ? resp.technicalMessage()
                : (nonBlank(resp.value()) ? resp.value() : missingKeyTechnical(key));
        final String client = nonBlank(resp.clientMessage()) ? resp.clientMessage()
                : (nonBlank(resp.value()) ? resp.value() : missingKeyClient());
        final String source = (nonBlank(resp.technicalMessage()) || nonBlank(resp.clientMessage()) || nonBlank(resp.value()))
                ? "message-service" : "backend-fallback";
        LOGGER.info("MessageClient → respuesta desde {} (key='{}')", source, key);
        return new MessageResult(technical, client, source);
    }

    private Mono<MessageResult> handleResponse(final String key, final HttpStatusCode status,
            final ClientResponse response) {
        if (status.is2xxSuccessful()) {
            return response.bodyToMono(RemoteMessageResponse.class)
                    .map(payload -> {
                        final MessageResult result = mapToResultOrDefault(key, payload);
                        return result != null ? result : new MessageResult(missingKeyTechnical(key), missingKeyClient(),
                                "backend-fallback");
                    });
        }
        LOGGER.warn("MessageClient → status {} al consultar '{}'. Respuesta vacía.", status.value(), key);
        return response.bodyToMono(String.class).then(Mono.empty());
    }

    private static boolean nonBlank(final String s) {
        return s != null && !s.isBlank();
    }

    private String missingKeyTechnical(final String key) {
        return "Technical error: missing message key " + key + ".";
    }

    private String missingKeyClient() {
        return "Ocurrió un error al procesar tu solicitud, por favor inténtalo de nuevo.";
    }

    public static MessageClient fallback() {
        return new MessageClient(WebClient.builder().baseUrl("http://localhost").build()) {
            @Override
            public Mono<MessageResult> getMessage(final String key) {
                return Mono.empty();
            }
        };
    }
}
