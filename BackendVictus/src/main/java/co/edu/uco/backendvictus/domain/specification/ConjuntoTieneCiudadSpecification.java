package co.edu.uco.backendvictus.domain.specification;

import co.edu.uco.backendvictus.domain.model.Ciudad;
import co.edu.uco.backendvictus.domain.model.ConjuntoResidencial;

/**
 * Ensures that a residential complex is linked to a valid city hierarchy.
 */
public final class ConjuntoTieneCiudadSpecification implements Specification<ConjuntoResidencial> {

    public static final ConjuntoTieneCiudadSpecification INSTANCE = new ConjuntoTieneCiudadSpecification();

    private ConjuntoTieneCiudadSpecification() {
    }

    @Override
    public boolean isSatisfiedBy(final ConjuntoResidencial candidate) {
        final Ciudad ciudad = candidate != null ? candidate.getCiudad() : null;
        return ciudad != null && ciudad.getDepartamento() != null && ciudad.getDepartamento().getPais() != null;
    }
}
