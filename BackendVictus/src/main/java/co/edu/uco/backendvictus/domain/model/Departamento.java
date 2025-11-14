package co.edu.uco.backendvictus.domain.model;

import java.util.UUID;

import co.edu.uco.backendvictus.crosscutting.helpers.ValidationUtils;
import co.edu.uco.backendvictus.domain.specification.DepartamentoTienePaisSpecification;
import co.edu.uco.backendvictus.domain.specification.SpecificationValidator;

/**
 * Department aggregate.
 */
public final class Departamento {

    private final UUID id;
    private final String nombre;
    private final Pais pais;

    private Departamento(final UUID id, final String nombre, final Pais pais) {
        this.id = id; //ValidationUtils.validateUUID(id, "Id del departamento");
        this.nombre = ValidationUtils.validateRequiredText(nombre, "Nombre del departamento", 120);
        this.pais = pais;

        SpecificationValidator.check(DepartamentoTienePaisSpecification.INSTANCE, this,
                "El departamento debe pertenecer a un pais valido");
    }

    public static Departamento create(final UUID id, final String nombre, final Pais pais) {
        return new Departamento(id, nombre, pais);
    }

    public Departamento update(final String nombre, final Pais pais) {
        return new Departamento(this.id, nombre, pais);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Pais getPais() {
        return pais;
    }
}
