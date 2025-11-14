package co.edu.uco.backendvictus.crosscutting.helpers;

import java.util.UUID;

/**
 * Centralizes UUID generation so domain and application layers can share the
 * same normalization strategy when instantiating aggregates.
 */
public final class UuidGenerator {

    private UuidGenerator() {
        // Utility class
    }

    public static UUID generate() {
        return UUID.randomUUID();
    }
}
