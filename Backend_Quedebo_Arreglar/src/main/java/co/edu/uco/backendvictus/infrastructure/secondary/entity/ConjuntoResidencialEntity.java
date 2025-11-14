package co.edu.uco.backendvictus.infrastructure.secondary.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("conjunto_residencial")
public class ConjuntoResidencialEntity implements Persistable<UUID> {

    @Id
    private final UUID id;

    @Column("nombre")
    private final String nombre;

    @Column("direccion")
    private final String direccion;

    @Column("ciudad_id")
    private final UUID ciudadId;

    @Column("administrador_id")
    private final UUID administradorId;

    @Column("telefono")
    private final String telefono;

    @Transient
    private final boolean isNew;

    @PersistenceCreator
    public ConjuntoResidencialEntity(final UUID id, final String nombre, final String direccion,
            final UUID ciudadId, final UUID administradorId, final String telefono) {
            // Constructor usado por el framework al leer desde la BD: la entidad no es nueva
        this(id, nombre, direccion, ciudadId, administradorId, telefono, false);
    }

    // Constructor para creación desde la capa de dominio (permite marcar la entidad como nueva)
    public ConjuntoResidencialEntity(final UUID id, final String nombre, final String direccion,
            final UUID ciudadId, final UUID administradorId, final String telefono, final boolean isNew) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudadId = ciudadId;
        this.administradorId = administradorId;
        this.telefono = telefono;
        this.isNew = isNew;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public UUID getCiudadId() {
        return ciudadId;
    }

    public UUID getAdministradorId() {
        return administradorId;
    }

    public String getTelefono() {
        return telefono;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
