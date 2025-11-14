package co.edu.uco.backendvictus.infrastructure.primary.response;

import java.time.OffsetDateTime;

public record ApiSuccessResponse<T>(boolean success, T data, OffsetDateTime timestamp) {

    public static <T> ApiSuccessResponse<T> of(final T data) {
        return new ApiSuccessResponse<>(true, data, OffsetDateTime.now());
    }
}
