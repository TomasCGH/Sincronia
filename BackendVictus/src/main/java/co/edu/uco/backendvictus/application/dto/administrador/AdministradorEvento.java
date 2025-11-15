package co.edu.uco.backendvictus.application.dto.administrador;

import java.util.Objects;

public final class AdministradorEvento {
    private final String tipo; // CREATED, UPDATED, DELETED
    private final AdministradorCatalogResponse payload;

    public AdministradorEvento(final String tipo, final AdministradorCatalogResponse payload) {
        this.tipo = Objects.requireNonNull(tipo, "tipo");
        this.payload = Objects.requireNonNull(payload, "payload");
    }

    public String getTipo() { return tipo; }
    public AdministradorCatalogResponse getPayload() { return payload; }
}

