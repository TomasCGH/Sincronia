package co.edu.uco.backendvictus.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class CorsConfig implements WebFluxConfigurer {

    private static final String FRONTEND_ORIGIN = "http://localhost:5174";

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(FRONTEND_ORIGIN)
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(false);
        registry.addMapping("/uco-challenge/**")
                .allowedOrigins(FRONTEND_ORIGIN)
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(false);
    }
}

