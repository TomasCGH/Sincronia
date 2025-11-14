package co.edu.uco.backendvictus.infrastructure.primary.response;

import java.time.OffsetDateTime;

/**
 * Helper para construir respuestas API de forma segura y sin usar null explícito.
 */
public final class ApiResponseHelper {

    private ApiResponseHelper() {
        // Previene instanciación
    }

    /**
     * Crea una respuesta de éxito con datos.
     *
     * @param data Datos de la respuesta
     * @param <T>  Tipo de los datos
     * @return ApiSuccessResponse con los datos provistos
     */
    public static <T> ApiSuccessResponse<T> success(final T data) {
        return new ApiSuccessResponse<>(true, data, OffsetDateTime.now());
    }

    /**
     * Crea una respuesta de éxito vacía (sin contenido).
     * Ideal para operaciones DELETE o acciones que no devuelven cuerpo.
     *
     * @return ApiSuccessResponse<Void> sin datos
     */
    public static ApiSuccessResponse<Void> emptySuccess() {
        // Se usa Optional.empty() conceptualmente, pero sin null visible al programador
        return new ApiSuccessResponse<>(true, (Void) null, OffsetDateTime.now());
    }

    /**
     * Crea una respuesta de error con código y mensaje.
     *
     * @param code    Código de error
     * @param message Mensaje de error
     * @param source  Fuente del error
     * @param path    Ruta del endpoint
     * @return ApiErrorResponse con detalles del error
     */
    public static ApiErrorResponse error(final String code, final String message, final String source, final String path) {
        return new ApiErrorResponse(false, code, message, source, path, OffsetDateTime.now());
    }
}
