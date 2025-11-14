package co.edu.uco.backendvictus.infrastructure.secondary.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("vivienda")
public class ViviendaEntity {

    @Id
    private final UUID id;

    @Column("numero")
    private final String numero;

    @Column("tipo")
    private final String tipo;

    @Column("estado")
    private final String estado;

    @Column("conjunto_id")
    private final UUID conjuntoId;

    @PersistenceCreator
    public ViviendaEntity(final UUID id, final String numero, final String tipo, final String estado,
            final UUID conjuntoId) {
        this.id = id;
        this.numero = numero;
        this.tipo = tipo;
        this.estado = estado;
        this.conjuntoId = conjuntoId;
    }

    public UUID getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public String getTipo() {
        return tipo;
    }

    public String getEstado() {
        return estado;
    }

    public UUID getConjuntoId() {
        return conjuntoId;
    }
}
