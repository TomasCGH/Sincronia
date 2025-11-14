package co.edu.uco.backendvictus.domain.specification;

import co.edu.uco.backendvictus.domain.model.Departamento;
import co.edu.uco.backendvictus.domain.model.Pais;

/**
 * Validates that a department belongs to an existing country.
 */
public final class DepartamentoTienePaisSpecification implements Specification<Departamento> {

    public static final DepartamentoTienePaisSpecification INSTANCE = new DepartamentoTienePaisSpecification();

    private DepartamentoTienePaisSpecification() {
    }

    @Override
    public boolean isSatisfiedBy(final Departamento candidate) {
        final Pais pais = candidate != null ? candidate.getPais() : null;
        return pais != null;
    }
}
