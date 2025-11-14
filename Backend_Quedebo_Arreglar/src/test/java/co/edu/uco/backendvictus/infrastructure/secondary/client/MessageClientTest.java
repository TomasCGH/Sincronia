package co.edu.uco.backendvictus.infrastructure.secondary.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

class MessageClientTest {

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
    void shouldGetMessageFromService_ValueField() {
        server.enqueue(new MockResponse()
                .setBody("{\"key\":\"domain.conjunto.nombre.duplicated\",\"value\":\"Mensaje OK\"}")
                .addHeader("Content-Type", "application/json"));

        final String baseUrl = server.url("/api/v1/messages").toString();
        final WebClient client = WebClient.builder().baseUrl(baseUrl).build();
        final MessageClient messageClient = new MessageClient(client);

        StepVerifier.create(messageClient.getMessage("domain.conjunto.nombre.duplicated"))
                .expectNextMatches(res -> res.clientMessage().equals("Mensaje OK") && res.technicalMessage().equals("Mensaje OK") && res.source().equals("message-service"))
                .verifyComplete();
    }

    @Test
    void shouldGetMessageFromService_TechnicalAndClient() {
        server.enqueue(new MockResponse()
                .setBody("{\"key\":\"domain.conjunto.nombre.duplicated\",\"technicalMessage\":\"Tech msg\",\"clientMessage\":\"Client msg\"}")
                .addHeader("Content-Type", "application/json"));

        final String baseUrl = server.url("/api/v1/messages").toString();
        final WebClient client = WebClient.builder().baseUrl(baseUrl).build();
        final MessageClient messageClient = new MessageClient(client);

        StepVerifier.create(messageClient.getMessage("domain.conjunto.nombre.duplicated"))
                .expectNextMatches(res -> res.clientMessage().equals("Client msg") && res.technicalMessage().equals("Tech msg") && res.source().equals("message-service"))
                .verifyComplete();
    }

    @Test
    void shouldUseMissingKeyDefaultsOn404() {
        server.enqueue(new MockResponse().setResponseCode(404));

        final String baseUrl = server.url("/api/v1/messages").toString();
        final WebClient client = WebClient.builder().baseUrl(baseUrl).build();
        final MessageClient messageClient = new MessageClient(client);

        StepVerifier.create(messageClient.getMessage("not.exists"))
                .verifyComplete();
    }

    @Test
    void shouldFallbackOnServerError() {
        server.enqueue(new MockResponse().setResponseCode(500));

        final String baseUrl = server.url("/api/v1/messages").toString();
        final WebClient client = WebClient.builder().baseUrl(baseUrl).build();
        final MessageClient messageClient = new MessageClient(client);

        StepVerifier.create(messageClient.getMessage("any.key"))
                .verifyComplete();
    }
}
