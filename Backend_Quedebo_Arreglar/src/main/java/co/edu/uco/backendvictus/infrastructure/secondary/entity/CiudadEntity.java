package co.edu.uco.backendvictus.infrastructure.secondary.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("ciudad")
public class CiudadEntity {

    @Id
    private final UUID id;

    @Column("departamento_id")
    private final UUID departamentoId;

    @Column("nombre")
    private final String nombre;

    @PersistenceCreator
    public CiudadEntity(final UUID id, final UUID departamentoId, final String nombre) {
        this.id = id;
        this.departamentoId = departamentoId;
        this.nombre = nombre;
    }

    public UUID getId() {
        return id;
    }

    public UUID getDepartamentoId() {
        return departamentoId;
    }

    public String getNombre() {
        return nombre;
    }
}
