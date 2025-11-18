package co.edu.uco.backendvictus.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class CorsConfig implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**") // Aplica CORS a TODAS las rutas
                .allowedOriginPatterns("*") // En producción reemplazar por dominio fijo
                .allowedMethods("*") // Permitimos todos los métodos
                .allowedHeaders("*") // Permitimos todos los headers (evita fallos preflight)
                .allowCredentials(false); // En WebFlux debe ser false si usas "*"
    }
}
