package co.edu.uco.backendvictus.infrastructure.secondary.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("conjunto_residencial")
public class ConjuntoResidencialEntity {

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

    @PersistenceCreator
    public ConjuntoResidencialEntity(final UUID id, final String nombre, final String direccion,
            final UUID ciudadId, final UUID administradorId, final String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudadId = ciudadId;
        this.administradorId = administradorId;
        this.telefono = telefono;
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
}
