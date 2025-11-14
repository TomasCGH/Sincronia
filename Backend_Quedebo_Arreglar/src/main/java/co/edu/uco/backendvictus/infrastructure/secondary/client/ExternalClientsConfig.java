package co.edu.uco.backendvictus.infrastructure.secondary.client;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import co.edu.uco.backendvictus.crosscutting.helpers.LoggerHelper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExternalClientsConfig {

    private static final Logger LOGGER = LoggerHelper.getLogger(ExternalClientsConfig.class);

    @Bean
    @Qualifier("messageWebClient")
    public WebClient messageWebClient(@Value("${services.message.base-url}") final String baseUrl) {
        LOGGER.info("Inicializando MessageClient con base URL {}", baseUrl);
        return buildWebClient(baseUrl);
    }

    @Bean
    @Qualifier("parameterWebClient")
    public WebClient parameterWebClient(@Value("${services.parameter.base-url}") final String baseUrl) {
        LOGGER.info("Inicializando ParameterClient con base URL {}", baseUrl);
        return buildWebClient(baseUrl);
    }

    @Bean
    public MessageClient messageClient(@Qualifier("messageWebClient") final WebClient messageWebClient) {
        return new MessageClient(messageWebClient);
    }

    @Bean
    public ParameterClient parameterClient(@Qualifier("parameterWebClient") final WebClient parameterWebClient) {
        return new ParameterClient(parameterWebClient);
    }

    private WebClient buildWebClient(final String baseUrl) {
        final HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                .responseTimeout(Duration.ofSeconds(3))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(3, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(3, TimeUnit.SECONDS))
                );

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            LOGGER.info("WebClient → {} {}", clientRequest.method(), clientRequest.url());
            return reactor.core.publisher.Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            LOGGER.info("WebClient ← status {}", clientResponse.statusCode());
            return reactor.core.publisher.Mono.just(clientResponse);
        });
    }
}
