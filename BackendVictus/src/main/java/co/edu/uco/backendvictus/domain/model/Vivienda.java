package co.edu.uco.backendvictus.domain.model;

import java.util.UUID;

import co.edu.uco.backendvictus.crosscutting.helpers.ValidationUtils;
import co.edu.uco.backendvictus.domain.specification.SpecificationValidator;
import co.edu.uco.backendvictus.domain.specification.ViviendaTieneConjuntoSpecification;

/**
 * Housing aggregate representing an individual unit inside a residential complex.
 */
public final class Vivienda {

    private final UUID id;
    private final String numero;
    private final ViviendaTipo tipo;
    private final ViviendaEstado estado;
    private final ConjuntoResidencial conjunto;

    private Vivienda(final UUID id, final String numero, final ViviendaTipo tipo, final ViviendaEstado estado,
            final ConjuntoResidencial conjunto) {
        this.id = id; //ValidationUtils.validateUUID(id, "Id de la vivienda");
        this.numero = ValidationUtils.validateRequiredText(numero, "Numero de la vivienda", 10).toUpperCase();
        this.tipo = tipo;
        this.estado = estado;
        this.conjunto = conjunto;

        SpecificationValidator.check(ViviendaTieneConjuntoSpecification.INSTANCE, this,
                "La vivienda debe pertenecer a un conjunto residencial valido");
    }

    public static Vivienda create(final UUID id, final String numero, final ViviendaTipo tipo,
            final ViviendaEstado estado, final ConjuntoResidencial conjunto) {
        return new Vivienda(id, numero, tipo, estado == null ? ViviendaEstado.DISPONIBLE : estado, conjunto);
    }

    public Vivienda update(final String numero, final ViviendaTipo tipo, final ViviendaEstado estado,
            final ConjuntoResidencial conjunto) {
        return new Vivienda(this.id, numero, tipo, estado, conjunto);
    }

    public Vivienda changeEstado(final ViviendaEstado nuevoEstado) {
        return new Vivienda(this.id, this.numero, this.tipo, nuevoEstado, this.conjunto);
    }

    public UUID getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public ViviendaTipo getTipo() {
        return tipo;
    }

    public ViviendaEstado getEstado() {
        return estado;
    }

    public ConjuntoResidencial getConjunto() {
        return conjunto;
    }
}
