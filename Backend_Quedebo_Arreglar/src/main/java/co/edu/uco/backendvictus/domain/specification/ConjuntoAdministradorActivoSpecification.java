package co.edu.uco.backendvictus.domain.specification;

import co.edu.uco.backendvictus.domain.model.conjunto.ConjuntoResidencial;

/**
 * Validates that the residential complex is linked to an active administrator.
 */
public final class ConjuntoAdministradorActivoSpecification implements Specification<ConjuntoResidencial> {

    public static final ConjuntoAdministradorActivoSpecification INSTANCE =
            new ConjuntoAdministradorActivoSpecification();

    private ConjuntoAdministradorActivoSpecification() {
    }

    @Override
    public boolean isSatisfiedBy(final ConjuntoResidencial candidate) {
        if (candidate == null) {
            return false;
        }
        return AdministradorActivoSpecification.INSTANCE.isSatisfiedBy(candidate.getAdministrador());
    }
}
