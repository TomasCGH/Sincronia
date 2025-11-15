package co.edu.uco.backendvictus.application.dto.departamento;

import java.util.Objects;

import co.edu.uco.backendvictus.application.dto.evento.TipoEvento;

/**
 * Evento SSE para departamentos.
 */
public final class DepartamentoEvento {

    private final String tipo;
    private final DepartamentoResponse payload;

    public DepartamentoEvento(final String tipo, final DepartamentoResponse payload) {
        this.tipo = Objects.requireNonNull(tipo, "tipo");
        this.payload = Objects.requireNonNull(payload, "payload");
    }

    public static DepartamentoEvento of(final TipoEvento tipo, final DepartamentoResponse payload) {
        return new DepartamentoEvento(tipo.name(), payload);
    }

    public String getTipo() {
        return tipo;
    }

    public DepartamentoResponse getPayload() {
        return payload;
    }
}
