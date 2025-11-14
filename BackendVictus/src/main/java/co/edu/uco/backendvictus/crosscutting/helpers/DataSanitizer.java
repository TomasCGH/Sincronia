package co.edu.uco.backendvictus.crosscutting.helpers;

import java.text.Normalizer;

/**
 * Utility class that centralizes input sanitization to avoid duplicated logic across layers.
 */
public final class DataSanitizer {

    private DataSanitizer() {
        // Utility class
    }

    /**
     * Removes leading/trailing spaces, replaces multiple spaces with a single one and strips dangerous characters.
     *
     * @param rawValue raw input provided by clients
     * @return sanitized text safe to be stored in the domain
     */
    public static String sanitizeText(final String rawValue) {
        if (rawValue == null) {
            return null;
        }

        String value = Normalizer.normalize(rawValue, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "") // remove diacritics
                .replaceAll("[^\\p{Alnum}\\s@._-]", "");
        value = value.trim();
        value = value.replaceAll("\\s+", " ");
        return value;
    }
}
