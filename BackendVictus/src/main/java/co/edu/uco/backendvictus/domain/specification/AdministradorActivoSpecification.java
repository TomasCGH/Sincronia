package co.edu.uco.backendvictus.domain.specification;

import co.edu.uco.backendvictus.domain.model.Administrador;

/**
 * Placeholder specification; previously validated 'activo'. Now always true to keep pipeline simple.
 */
public final class AdministradorActivoSpecification implements Specification<Administrador> {

    public static final AdministradorActivoSpecification INSTANCE = new AdministradorActivoSpecification();

    private AdministradorActivoSpecification() {
    }

    @Override
    public boolean isSatisfiedBy(final Administrador candidate) {
        return candidate != null;
    }
}
