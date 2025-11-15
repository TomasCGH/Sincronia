package co.edu.uco.backendvictus.application.dto.ciudad;

import java.util.Objects;

import co.edu.uco.backendvictus.application.dto.evento.TipoEvento;

/**
 * Evento SSE para ciudades.
 */
public final class CiudadEvento {

    private final String tipo;
    private final CiudadResponse payload;

    public CiudadEvento(final String tipo, final CiudadResponse payload) {
        this.tipo = Objects.requireNonNull(tipo, "tipo");
        this.payload = Objects.requireNonNull(payload, "payload");
    }

    public static CiudadEvento of(final TipoEvento tipo, final CiudadResponse payload) {
        return new CiudadEvento(tipo.name(), payload);
    }

    public String getTipo() {
        return tipo;
    }

    public CiudadResponse getPayload() {
        return payload;
    }
}
