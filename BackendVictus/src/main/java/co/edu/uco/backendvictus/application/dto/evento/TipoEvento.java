package co.edu.uco.backendvictus.application.dto.evento;

import java.util.Locale;
import java.util.Optional;

/**
 * Tipo genérico de evento para operaciones CRUD expuestas vía streaming.
 */
public enum TipoEvento {
    CREATED,
    UPDATED,
    DELETED;

    /**
     * Intenta convertir una cadena arbitraria (por ejemplo, recibida desde PostgreSQL NOTIFY) en un {@link TipoEvento}.
     * Retorna {@link Optional#empty()} si la cadena es nula o no coincide con ninguno de los valores conocidos.
     */
    public static Optional<TipoEvento> fromString(final String raw) {
        if (raw == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(TipoEvento.valueOf(raw.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
