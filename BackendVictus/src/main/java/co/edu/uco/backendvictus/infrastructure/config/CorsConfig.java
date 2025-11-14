package co.edu.uco.backendvictus.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        // Configuración general (API REST normal)
        CorsConfiguration general = new CorsConfiguration();
        general.setAllowedOrigins(List.of("http://localhost:5174"));
        general.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        general.setAllowedHeaders(List.of("*"));
        general.setAllowCredentials(true);

        // Configuración específica para SSE: solo GET y sin credenciales
        CorsConfiguration sse = new CorsConfiguration();
        sse.setAllowedOrigins(List.of("http://localhost:5174"));
        sse.setAllowedMethods(List.of("GET"));
        sse.setAllowedHeaders(List.of("*"));
        sse.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/api/**", general);
        source.registerCorsConfiguration("/uco-challenge/**", general);
        source.registerCorsConfiguration("/uco-challenge/api/v1/conjuntos/stream", sse);
        source.registerCorsConfiguration("/api/v1/departamentos/stream", sse);
        source.registerCorsConfiguration("/api/v1/ciudades/stream", sse);
        return new CorsWebFilter(source);
    }
}
