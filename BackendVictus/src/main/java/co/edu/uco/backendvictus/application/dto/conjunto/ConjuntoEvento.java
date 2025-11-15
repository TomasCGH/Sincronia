package co.edu.uco.backendvictus.application.dto.conjunto;

import java.util.Objects;

import co.edu.uco.backendvictus.application.dto.evento.TipoEvento;

/**
 * Evento SSE para los conjuntos residenciales.
 */
public final class ConjuntoEvento {

    private final String tipo;
    private final ConjuntoResponse payload;

    public ConjuntoEvento(final String tipo, final ConjuntoResponse payload) {
        this.tipo = Objects.requireNonNull(tipo, "tipo");
        this.payload = Objects.requireNonNull(payload, "payload");
    }

    public static ConjuntoEvento of(final TipoEvento tipo, final ConjuntoResponse payload) {
        return new ConjuntoEvento(tipo.name(), payload);
    }

    public String getTipo() {
        return tipo;
    }

    public ConjuntoResponse getPayload() {
        return payload;
    }
}
