package co.edu.uco.backendvictus.infrastructure.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import java.util.Optional;

@Configuration
public class OptionalServiceConfig {

    private static final Logger log = LoggerFactory.getLogger(OptionalServiceConfig.class);

    @Value("${services.message.base-url:}")
    private String messageServiceUrl;

    @Value("${services.parameter.base-url:}")
    private String parameterServiceUrl;

    @PostConstruct
    public void validateOptionalServices() {
        if (messageServiceUrl == null || messageServiceUrl.isBlank()) {
            log.warn("services.message.base-url is not configured. Message features will be disabled or limited.");
        } else {
            log.info("services.message.base-url={}", messageServiceUrl);
        }

        if (parameterServiceUrl == null || parameterServiceUrl.isBlank()) {
            log.warn("services.parameter.base-url is not configured. Parameter features will be disabled or limited.");
        } else {
            log.info("services.parameter.base-url={}", parameterServiceUrl);
        }
    }

    @Bean
    public Optional<String> messageServiceBaseUrl() {
        return Optional.ofNullable(messageServiceUrl == null || messageServiceUrl.isBlank() ? null : messageServiceUrl);
    }

    @Bean
    public Optional<String> parameterServiceBaseUrl() {
        return Optional.ofNullable(parameterServiceUrl == null || parameterServiceUrl.isBlank() ? null : parameterServiceUrl);
    }
}

