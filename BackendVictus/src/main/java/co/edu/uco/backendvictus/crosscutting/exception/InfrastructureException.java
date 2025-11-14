package co.edu.uco.backendvictus.crosscutting.exception;

/**
 * Represents errors thrown by infrastructure adapters.
 */
public class InfrastructureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InfrastructureException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
