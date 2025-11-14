package co.edu.uco.backendvictus.infrastructure.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyVaultStartupLogger {

    private static final Logger log = LoggerFactory.getLogger(KeyVaultStartupLogger.class);

    @Value("${spring.r2dbc.url:}")
    private String r2dbcUrl;

    @Value("${spring.r2dbc.username:}")
    private String r2dbcUsername;

    @PostConstruct
    public void logSecrets() {
        if (r2dbcUrl == null || r2dbcUrl.isBlank()) {
            log.warn("spring.r2dbc.url is not set");
        } else {
            log.info("spring.r2dbc.url is set: {}", r2dbcUrl);
        }

        if (r2dbcUsername == null || r2dbcUsername.isBlank()) {
            log.warn("spring.r2dbc.username is not set");
        } else {
            log.info("spring.r2dbc.username is set: {}", r2dbcUsername);
        }
    }
}

