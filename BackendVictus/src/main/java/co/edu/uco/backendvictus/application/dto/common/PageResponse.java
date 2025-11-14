package co.edu.uco.backendvictus.application.dto.common;

import java.util.List;

public record PageResponse<T>(List<T> items, long total, int page, int size) {

    public static <T> PageResponse<T> of(final List<T> items, final long total, final int page, final int size) {
        return new PageResponse<>(items, total, page, size);
    }
}
