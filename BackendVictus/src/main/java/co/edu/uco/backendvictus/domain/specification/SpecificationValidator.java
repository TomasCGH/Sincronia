package co.edu.uco.backendvictus.domain.specification;

import co.edu.uco.backendvictus.crosscutting.exception.DomainException;

/**
 * Helper to enforce specifications inside domain entities.
 */
public final class SpecificationValidator {

    private SpecificationValidator() {
    }

    public static <T> void check(final Specification<T> specification, final T candidate, final String message) {
        if (!specification.isSatisfiedBy(candidate)) {
            throw new DomainException(message);
        }
    }
}
