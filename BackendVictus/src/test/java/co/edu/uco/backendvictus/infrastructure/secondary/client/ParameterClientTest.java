package co.edu.uco.backendvictus.infrastructure.secondary.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

class ParameterClientTest {

    private MockWebServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void shouldGetParameterFromService() {
        server.enqueue(new MockResponse()
                .setBody("{\"key\":\"conjunto.max.limit\",\"value\":\"500\"}")
                .addHeader("Content-Type", "application/json"));

        final String baseUrl = server.url("/api/v1/parameters").toString();
        final WebClient client = WebClient.builder().baseUrl(baseUrl).build();
        final ParameterClient parameterClient = new ParameterClient(client);

        StepVerifier.create(parameterClient.get("conjunto.max.limit"))
                .expectNextMatches(res -> res.key().equals("conjunto.max.limit") && "500".equals(res.value()) && res.source().equals("parameter-service"))
                .verifyComplete();
    }

    @Test
    void shouldFallbackOnConnectivityError() throws IOException {
        // No encolamos respuesta y apagamos el server para simular caída
        final String baseUrl = server.url("/api/v1/parameters").toString();
        server.shutdown();

        final WebClient client = WebClient.builder().baseUrl(baseUrl).build();
        final ParameterClient parameterClient = new ParameterClient(client);

        StepVerifier.create(parameterClient.get("conjunto.max.limit"))
                .verifyComplete();
    }
}

