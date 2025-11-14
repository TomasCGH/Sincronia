package co.edu.uco.backendvictus.infrastructure.primary.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.crosscutting.exception.DomainException;
import co.edu.uco.backendvictus.crosscutting.exception.InfrastructureException;
import co.edu.uco.backendvictus.crosscutting.helpers.LoggerHelper;
import co.edu.uco.backendvictus.infrastructure.secondary.client.MessageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
import java.time.OffsetDateTime;
import reactor.core.publisher.Mono;
import co.edu.uco.backendvictus.infrastructure.primary.response.ApiErrorResponse;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    private static final org.slf4j.Logger LOGGER = LoggerHelper.getLogger(ExceptionHandlerAdvice.class);

    private final MessageClient messageClient;

    @Autowired
    public ExceptionHandlerAdvice(final MessageClient messageClient) {
        this.messageClient = messageClient;
    }

    @ExceptionHandler(DomainException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleDomainException(final DomainException exception,
            final ServerWebExchange exchange) {
        LOGGER.warn("Error de dominio: {}", exception.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "DOMAIN_ERROR", exception, exchange, "backend");
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleValidationErrors(final WebExchangeBindException ex,
            final ServerWebExchange exchange) {
        final String messageKey = ex.getAllErrors().stream()
                .findFirst()
                .map(ObjectError::getDefaultMessage)
                .orElse("validation.general");

        return messageClient.getMessage(messageKey)
                .switchIfEmpty(messageClient.getMessage("validation.general"))
                .flatMap(msg -> {
                    final ApiErrorResponse response = new ApiErrorResponse(false, "APPLICATION_ERROR",
                            msg.clientMessage(), msg.source(), exchange.getRequest().getPath().value(), OffsetDateTime.now());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response));
                });
    }

    @ExceptionHandler(ApplicationException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleApplicationException(final ApplicationException exception,
            final ServerWebExchange exchange) {
        LOGGER.warn("Error de aplicacion: {}", exception.getMessage());
        final String source = exception.getSource() != null ? exception.getSource() : "backend";
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "APPLICATION_ERROR", exception, exchange, source);
    }

    @ExceptionHandler(InfrastructureException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleInfrastructureException(
            final InfrastructureException exception, final ServerWebExchange exchange) {
        LOGGER.error("Error de infraestructura", exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INFRASTRUCTURE_ERROR", exception, exchange, "backend");
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleGenericException(final Exception exception,
            final ServerWebExchange exchange) {
        LOGGER.error("Error inesperado", exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", exception, exchange, "backend");
    }

    private Mono<ResponseEntity<ApiErrorResponse>> buildResponse(final HttpStatus status, final String code,
            final Exception exception, final ServerWebExchange exchange, final String source) {
        final String path = exchange.getRequest().getPath().value();
        final ApiErrorResponse response = ApiErrorResponse.of(code, exception.getMessage(), source, path);
        return Mono.just(ResponseEntity.status(status).body(response));
    }
}
