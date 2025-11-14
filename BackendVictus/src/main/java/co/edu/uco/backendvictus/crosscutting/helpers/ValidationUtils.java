package co.edu.uco.backendvictus.crosscutting.helpers;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import co.edu.uco.backendvictus.crosscutting.exception.DomainException;

/**
 * Provides cross-cutting validation logic shared across the different layers.
 */
public final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    private ValidationUtils() {
        // Utility class
    }

    public static UUID validateUUID(final UUID id, final String fieldName) {
        if (Objects.isNull(id)) {
            throw new DomainException(fieldName + " no puede ser nulo");
        }
        return id;
    }

    public static String validateRequiredText(final String value, final String fieldName, final int maxLength) {
        final String sanitized = DataSanitizer.sanitizeText(value);
        if (sanitized == null || sanitized.isBlank()) {
            throw new DomainException(fieldName + " es requerido");
        }
        if (sanitized.length() > maxLength) {
            throw new DomainException(fieldName + " supera la longitud maxima de " + maxLength);
        }
        return sanitized;
    }

    public static String validateOptionalText(final String value, final String fieldName, final int maxLength) {
        final String sanitized = DataSanitizer.sanitizeText(value);
        if (sanitized != null && sanitized.length() > maxLength) {
            throw new DomainException(fieldName + " supera la longitud maxima de " + maxLength);
        }
        return sanitized;
    }

    public static String validateEmail(final String value) {
        final String sanitized = DataSanitizer.sanitizeText(value);
        if (sanitized == null || sanitized.isBlank()) {
            throw new DomainException("El correo electronico es requerido");
        }
        if (!EMAIL_PATTERN.matcher(sanitized).matches()) {
            throw new DomainException("El correo electronico no es valido");
        }
        return sanitized;
    }
}
