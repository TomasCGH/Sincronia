package co.edu.uco.backendvictus.domain.specification;

import co.edu.uco.backendvictus.domain.model.conjunto.ConjuntoResidencial;
import co.edu.uco.backendvictus.domain.model.Vivienda;

/**
 * Ensures that a housing unit is always linked to a residential complex.
 */
public final class ViviendaTieneConjuntoSpecification implements Specification<Vivienda> {

    public static final ViviendaTieneConjuntoSpecification INSTANCE = new ViviendaTieneConjuntoSpecification();

    private ViviendaTieneConjuntoSpecification() {
    }

    @Override
    public boolean isSatisfiedBy(final Vivienda candidate) {
        final ConjuntoResidencial conjunto = candidate != null ? candidate.getConjunto() : null;
        return conjunto != null;
    }
}
