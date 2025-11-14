package co.edu.uco.backendvictus;

import co.edu.uco.backendvictus.infrastructure.config.AzureKeyVaultEnvironmentPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

@SpringBootApplication
public class BackendVictusApplication {

    private static final Logger log = LoggerFactory.getLogger(BackendVictusApplication.class);

    public static void main(final String[] args) {
        // Ensure compatibility verifier is disabled as early as possible
        System.setProperty("spring.cloud.azure.compatibility-verifier.enabled", "false");

        SpringApplication app = new SpringApplication(BackendVictusApplication.class);

        // Crear un environment y precargar application.properties para que el post-processor lo lea
        ConfigurableEnvironment env = new StandardEnvironment();
        try {
            var resource = new ClassPathResource("application.properties");
            if (resource.exists()) {
                env.getPropertySources().addLast(new ResourcePropertySource("applicationProperties", resource));
                log.info("Loaded application.properties into pre-start environment.");
            } else {
                log.warn("application.properties not found on classpath; proceeding without preloaded properties.");
            }
        } catch (IOException e) {
            log.warn("Could not load application.properties into pre-start environment: {}", e.getMessage());
        }

        // Ejecutar manualmente el EnvironmentPostProcessor para cargar secretos desde Key Vault
        try {
            var postProcessor = new AzureKeyVaultEnvironmentPostProcessor();
            postProcessor.postProcessEnvironment(env, app);
            log.info("AzureKeyVaultEnvironmentPostProcessor executed before application start.");
        } catch (Exception e) {
            log.warn("Exception running AzureKeyVaultEnvironmentPostProcessor: {}", e.getMessage());
        }

        // Asignar el environment al SpringApplication y arrancar
        app.setEnvironment(env);
        app.run(args);
    }
}
