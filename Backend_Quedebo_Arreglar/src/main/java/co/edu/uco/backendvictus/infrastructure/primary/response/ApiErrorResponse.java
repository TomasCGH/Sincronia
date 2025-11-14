package co.edu.uco.backendvictus.infrastructure.primary.response;

import java.time.OffsetDateTime;

public record ApiErrorResponse(boolean success, String code, String message, String source, String path, OffsetDateTime timestamp) {

    public static ApiErrorResponse of(final String code, final String message, final String source, final String path) {
        return new ApiErrorResponse(false, code, message, source, path, OffsetDateTime.now());
    }
}
