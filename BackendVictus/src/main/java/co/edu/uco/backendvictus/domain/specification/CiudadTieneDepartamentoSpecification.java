package co.edu.uco.backendvictus.domain.specification;

import co.edu.uco.backendvictus.domain.model.Ciudad;
import co.edu.uco.backendvictus.domain.model.Departamento;

/**
 * Ensures that a city belongs to a department.
 */
public final class CiudadTieneDepartamentoSpecification implements Specification<Ciudad> {

    public static final CiudadTieneDepartamentoSpecification INSTANCE = new CiudadTieneDepartamentoSpecification();

    private CiudadTieneDepartamentoSpecification() {
    }

    @Override
    public boolean isSatisfiedBy(final Ciudad candidate) {
        final Departamento departamento = candidate != null ? candidate.getDepartamento() : null;
        return departamento != null && departamento.getPais() != null;
    }
}
