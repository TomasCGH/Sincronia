package co.edu.uco.backendvictus.domain.model;

import java.util.UUID;

import co.edu.uco.backendvictus.crosscutting.helpers.ValidationUtils;

/**
 * Administrator aggregate.
 */
public final class Administrador {

    private final UUID id;
    private final String primerNombre;
    private final String segundoNombres;
    private final String primerApellido;
    private final String segundoApellido;
    private final String email;
    private final String telefono;

    private Administrador(final UUID id, final String primerNombre, final String segundoNombres,
            final String primerApellido, final String segundoApellido, final String email, final String telefono) {
        this.id = id; //ValidationUtils.validateUUID(id, "Id del administrador");
        this.primerNombre = ValidationUtils.validateRequiredText(primerNombre, "Primer nombre", 60);
        this.segundoNombres = ValidationUtils.validateOptionalText(segundoNombres, "Segundo nombres", 100);
        this.primerApellido = ValidationUtils.validateRequiredText(primerApellido, "Primer apellido", 60);
        this.segundoApellido = ValidationUtils.validateOptionalText(segundoApellido, "Segundo apellido", 60);
        this.email = ValidationUtils.validateEmail(email);
        this.telefono = ValidationUtils.validateOptionalText(telefono, "Telefono", 20);
    }

    public static Administrador create(final UUID id, final String primerNombre, final String segundoNombres,
            final String primerApellido, final String segundoApellido, final String email, final String telefono) {
        return new Administrador(id, primerNombre, segundoNombres, primerApellido, segundoApellido, email, telefono);
    }

    public Administrador update(final String primerNombre, final String segundoNombres, final String primerApellido,
            final String segundoApellido, final String email, final String telefono) {
        return new Administrador(this.id, primerNombre, segundoNombres, primerApellido, segundoApellido, email,
                telefono);
    }

    public UUID getId() {
        return id;
    }

    public String getPrimerNombre() {
        return primerNombre;
    }

    public String getSegundoNombres() {
        return segundoNombres;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public String getNombreCompleto() {
        final StringBuilder builder = new StringBuilder(primerNombre);
        if (segundoNombres != null && !segundoNombres.isBlank()) {
            builder.append(' ').append(segundoNombres);
        }
        builder.append(' ').append(primerApellido);
        if (segundoApellido != null && !segundoApellido.isBlank()) {
            builder.append(' ').append(segundoApellido);
        }
        return builder.toString();
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }
}
