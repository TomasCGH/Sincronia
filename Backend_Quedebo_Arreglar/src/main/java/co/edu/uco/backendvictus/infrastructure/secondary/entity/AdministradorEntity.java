package co.edu.uco.backendvictus.infrastructure.secondary.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("administrador")
public class AdministradorEntity {

    @Id
    private final UUID id;

    @Column("primer_nombre")
    private final String primerNombre;

    @Column("segundo_nombre")
    private final String segundoNombre;

    @Column("primer_apellido")
    private final String primerApellido;

    @Column("segundo_apellido")
    private final String segundoApellido;

    @Column("correo")
    private final String correo;

    @Column("telefono")
    private final String telefono;

    @PersistenceCreator
    public AdministradorEntity(final UUID id, final String primerNombre, final String segundoNombre,
            final String primerApellido, final String segundoApellido, final String correo, final String telefono) {
        this.id = id;
        this.primerNombre = primerNombre;
        this.segundoNombre = segundoNombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.correo = correo;
        this.telefono = telefono;
    }

    public UUID getId() {
        return id;
    }

    public String getPrimerNombre() {
        return primerNombre;
    }

    public String getSegundoNombre() {
        return segundoNombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }
}
