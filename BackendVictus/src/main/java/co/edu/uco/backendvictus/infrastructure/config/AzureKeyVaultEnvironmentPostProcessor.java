package co.edu.uco.backendvictus.infrastructure.config;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class AzureKeyVaultEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(AzureKeyVaultEnvironmentPostProcessor.class);
    private static final String PROPERTY_SOURCE_NAME = "azure-key-vault-secrets-post-processor";
    private static final String KEY_VAULT_URL_PROPERTY = "spring.cloud.azure.keyvault.secret.endpoint";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String vaultUrl = environment.getProperty(KEY_VAULT_URL_PROPERTY);
        if (vaultUrl == null || vaultUrl.isBlank()) {
            log.warn("Azure Key Vault endpoint not configured ({}). Skipping secret load.", KEY_VAULT_URL_PROPERTY);
            return;
        }

        try {
            log.info("Starting AzureKeyVaultEnvironmentPostProcessor. Checking Key Vault endpoint: {}", vaultUrl);
            // Construir credencial y obtener token para el scope de Key Vault
            DefaultAzureCredential credential = new DefaultAzureCredentialBuilder().build();
            TokenRequestContext trc = new TokenRequestContext().addScopes("https://vault.azure.net/.default");
            AccessToken token = null;
            try {
                token = credential.getToken(trc).block(Duration.ofSeconds(30));
            } catch (Exception e) {
                log.warn("Exception while acquiring token from DefaultAzureCredential: {}", e.getMessage());
            }

            Map<String, Object> map = new HashMap<>();

            if (token == null) {
                log.warn("Could not acquire access token for Key Vault. Trying env/sysprops fallback for secrets.");

                // Intentar cargar desde variables de entorno (variantes comunes)
                String envUrl = firstNonBlank(System.getenv("SPRING_R2DBC_URL"), System.getenv("spring-r2dbc-url"),
                        environment.getProperty("spring-r2dbc-url"), System.getProperty("spring-r2dbc-url"));
                String envUser = firstNonBlank(System.getenv("SPRING_R2DBC_USERNAME"), System.getenv("spring-r2dbc-username"),
                        environment.getProperty("spring-r2dbc-username"), System.getProperty("spring-r2dbc-username"));
                String envPass = firstNonBlank(System.getenv("SPRING_R2DBC_PASSWORD"), System.getenv("spring-r2dbc-password"),
                        environment.getProperty("spring-r2dbc-password"), System.getProperty("spring-r2dbc-password"));

                if (envUrl != null) map.put("spring-r2dbc-url", envUrl);
                if (envUser != null) map.put("spring-r2dbc-username", envUser);
                if (envPass != null) map.put("spring-r2dbc-password", envPass);

                if (!map.isEmpty()) {
                    var ps = new MapPropertySource(PROPERTY_SOURCE_NAME, map);
                    environment.getPropertySources().addFirst(ps);
                    log.info("Loaded DB secrets from environment/system properties as fallback.");
                } else {
                    log.warn("No secrets found in environment/system properties fallback.");
                }

                return; // Ya intentamos fallback; salir
            }

            String bearer = token.getToken();
            HttpClient httpClient = HttpClient.newHttpClient();

            String[] secretNames = new String[]{"spring-r2dbc-url", "spring-r2dbc-username", "spring-r2dbc-password"};

            for (String secretName : secretNames) {
                try {
                    String secretEndpoint = String.format("%s/secrets/%s?api-version=7.4", vaultUrl.replaceAll("/$", ""), secretName);
                    HttpRequest req = HttpRequest.newBuilder()
                            .uri(URI.create(secretEndpoint))
                            .header("Authorization", "Bearer " + bearer)
                            .header("Accept", "application/json")
                            .GET()
                            .timeout(Duration.ofSeconds(10))
                            .build();

                    HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
                    if (resp.statusCode() == 200) {
                        String body = resp.body();
                        String value = extractJsonValue(body, "value");
                        if (value != null) {
                            map.put(secretName, value);
                            log.info("Loaded secret '{}' from Key Vault.", secretName);
                        } else {
                            log.warn("Secret '{}' fetched but 'value' not found in response.", secretName);
                        }
                    } else {
                        log.warn("Failed fetching secret '{}' from Key Vault. status={}, body={}", secretName, resp.statusCode(), resp.body());
                    }
                } catch (Exception ex) {
                    log.warn("Unable to load secret '{}' from Key Vault: {}", secretName, ex.getMessage());
                }
            }

            if (!map.isEmpty()) {
                var ps = new MapPropertySource(PROPERTY_SOURCE_NAME, map);
                environment.getPropertySources().addFirst(ps);
                log.info("Azure Key Vault secrets added to environment property sources.");
            } else {
                log.warn("No Key Vault secrets were loaded.");
            }

        } catch (Exception ex) {
            log.error("Error while initializing Azure Key Vault access: {}", ex.getMessage(), ex);
        }
    }

    // Very small and robust JSON extraction without adding dependencies
    private static String extractJsonValue(String json, String key) {
        if (json == null || key == null) return null;
        String pattern = '"' + key + '"' + ':';
        int idx = json.indexOf(pattern);
        if (idx == -1) return null;
        int start = json.indexOf('"', idx + pattern.length());
        if (start == -1) return null;
        start++;
        int end = json.indexOf('"', start);
        if (end == -1) return null;
        return json.substring(start, end);
    }

    private static String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }
}
