package co.edu.uco.backendvictus.domain.model;

import java.util.Arrays;

import co.edu.uco.backendvictus.crosscutting.exception.DomainException;
import co.edu.uco.backendvictus.crosscutting.helpers.DataSanitizer;

/**
 * Allowed housing statuses within the residential complex lifecycle.
 */
public enum ViviendaEstado {

    DISPONIBLE("Disponible"),
    OCUPADA("Ocupada"),
    EN_MANTENIMIENTO("En mantenimiento"),
    INACTIVA("Inactiva");

    private final String value;
    private final String normalized;

    ViviendaEstado(final String value) {
        this.value = value;
        this.normalized = normalize(value);
    }

    public String getValue() {
        return value;
    }

    public static ViviendaEstado from(final String rawValue) {
        final String normalizedInput = normalize(rawValue);
        return Arrays.stream(values())
                .filter(estado -> estado.normalized.equals(normalizedInput))
                .findFirst()
                .orElseThrow(() -> new DomainException("Estado de la vivienda no es valido"));
    }

    private static String normalize(final String value) {
        final String sanitized = DataSanitizer.sanitizeText(value);
        return sanitized == null ? null : sanitized.toUpperCase();
    }
}
