package co.edu.uco.backendvictus.domain.model;

import java.util.UUID;

import co.edu.uco.backendvictus.crosscutting.helpers.ValidationUtils;

/**
 * Country aggregate root.
 */
public final class Pais {

    private final UUID id;
    private final String nombre;

    private Pais(final UUID id, final String nombre) {
        this.id = id; //ValidationUtils.validateUUID(id, "Id del pais");
        this.nombre = ValidationUtils.validateRequiredText(nombre, "Nombre del pais", 120);
    }

    public static Pais create(final UUID id, final String nombre) {
        return new Pais(id, nombre);
    }

    public Pais update(final String nombre) {
        return new Pais(this.id, nombre);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}
