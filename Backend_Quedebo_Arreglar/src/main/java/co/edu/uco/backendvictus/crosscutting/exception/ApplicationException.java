package co.edu.uco.backendvictus.crosscutting.exception;

/**
 * Represents high level application errors raised in use cases.
 */
public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String source; // e.g., "message-service", "backend-fallback", etc.

    public ApplicationException(final String message) {
        super(message);
        this.source = "backend";
    }

    public ApplicationException(final String message, final Throwable cause) {
        super(message, cause);
        this.source = "backend";
    }

    public ApplicationException(final String message, final String source) {
        super(message);
        this.source = source;
    }

    public ApplicationException(final String message, final Throwable cause, final String source) {
        super(message, cause);
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
