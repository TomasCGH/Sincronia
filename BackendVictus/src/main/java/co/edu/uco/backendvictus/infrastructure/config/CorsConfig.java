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
        // Aplicar CORS a todas las rutas para evitar 403 por paths no cubiertos (p. ej. /uco/**)
        registry.addMapping("/**")
                // En desarrollo, permitir cualquier origen. En producción, reemplazar por dominios específicos.
                .allowedOriginPatterns("*")
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                // Permitir todos los headers para evitar fallos de preflight por encabezados personalizados
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}
