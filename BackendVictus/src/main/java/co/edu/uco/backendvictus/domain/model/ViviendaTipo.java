package co.edu.uco.backendvictus.domain.model;

import java.util.Arrays;

import co.edu.uco.backendvictus.crosscutting.exception.DomainException;
import co.edu.uco.backendvictus.crosscutting.helpers.DataSanitizer;

/**
 * Enumerates the supported housing types handled in the system.
 */
public enum ViviendaTipo {

    APARTAMENTO("Apartamento"),
    CASA("Casa"),
    ESTUDIO("Estudio"),
    DUPLEX("Dúplex");

    private final String value;
    private final String normalized;

    ViviendaTipo(final String value) {
        this.value = value;
        this.normalized = normalize(value);
    }

    public String getValue() {
        return value;
    }

    public static ViviendaTipo from(final String rawValue) {
        final String normalizedInput = normalize(rawValue);
        return Arrays.stream(values())
                .filter(tipo -> tipo.normalized.equals(normalizedInput))
                .findFirst()
                .orElseThrow(() -> new DomainException("Tipo de vivienda no es valido"));
    }

    private static String normalize(final String value) {
        final String sanitized = DataSanitizer.sanitizeText(value);
        return sanitized == null ? null : sanitized.toUpperCase();
    }
}
