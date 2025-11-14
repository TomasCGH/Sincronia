package co.edu.uco.backendvictus.application.usecase.conjunto;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.UUID;

import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoCreateRequest;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoEvento;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoResponse;
import co.edu.uco.backendvictus.application.mapper.ConjuntoApplicationMapper;
import co.edu.uco.backendvictus.application.port.out.conjunto.ConjuntoEventoPublisher;
import co.edu.uco.backendvictus.application.port.out.conjunto.ConjuntoRepositoryPort;
import co.edu.uco.backendvictus.application.usecase.UseCase;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.crosscutting.helpers.LoggerHelper;

import co.edu.uco.backendvictus.domain.model.Administrador;
import co.edu.uco.backendvictus.domain.model.Ciudad;
import co.edu.uco.backendvictus.domain.model.conjunto.ConjuntoResidencial;
import co.edu.uco.backendvictus.domain.port.AdministradorRepository;
import co.edu.uco.backendvictus.domain.port.CiudadRepository;
import co.edu.uco.backendvictus.infrastructure.secondary.client.MessageClient;
import co.edu.uco.backendvictus.infrastructure.secondary.client.ParameterClient;

@Service
public class CreateConjuntoUseCase implements UseCase<ConjuntoCreateRequest, ConjuntoResponse> {

    private static final Logger LOGGER = LoggerHelper.getLogger(CreateConjuntoUseCase.class);

    private final ConjuntoRepositoryPort conjuntoRepository;
    private final CiudadRepository ciudadRepository;
    private final AdministradorRepository administradorRepository;
    private final ConjuntoApplicationMapper mapper;
    private final MessageClient messageClient;
    private final ParameterClient parameterClient;
    private final ConjuntoEventoPublisher eventoPublisher;

    @Autowired
    public CreateConjuntoUseCase(final ConjuntoRepositoryPort conjuntoRepository,
            final CiudadRepository ciudadRepository, final AdministradorRepository administradorRepository,
            final ConjuntoApplicationMapper mapper, final MessageClient messageClient,
            final ParameterClient parameterClient, final ConjuntoEventoPublisher eventoPublisher) {
        this.conjuntoRepository = conjuntoRepository;
        this.ciudadRepository = ciudadRepository;
        this.administradorRepository = administradorRepository;
        this.mapper = mapper;
        this.messageClient = messageClient;
        this.parameterClient = parameterClient;
        this.eventoPublisher = eventoPublisher;
    }

    @Override
    public Mono<ConjuntoResponse> execute(final ConjuntoCreateRequest request) {

        if (request.ciudadId() == null || request.administradorId() == null) {
            return this.<ConjuntoResponse>errorFromMessage("validation.required.uuid");
        }

        if (request.telefono() == null || request.telefono().isBlank()) {
            return this.<ConjuntoResponse>errorFromMessage("validation.required.telefono");
        }

        if (!request.telefono().matches("^[0-9]+$")) {
            return this.<ConjuntoResponse>errorFromMessage("validation.format.telefono");
        }

        if (request.telefono().length() < 7 || request.telefono().length() > 10) {
            return this.<ConjuntoResponse>errorFromMessage("validation.length.telefono");
        }

        if (request.direccion() == null || request.direccion().isBlank()) {
            return this.<ConjuntoResponse>errorFromMessage("validation.required.direccion");
        }

        final Mono<Ciudad> ciudadMono = ciudadRepository.findById(request.ciudadId())
                .switchIfEmpty(Mono.error(new ApplicationException("Ciudad no encontrada", "backend")));
        final Mono<Administrador> administradorMono = administradorRepository.findById(request.administradorId())
                .switchIfEmpty(Mono.error(new ApplicationException("Administrador no encontrado", "backend")));

        return Mono.zip(ciudadMono, administradorMono)
                .flatMap(tuple -> {
                    final Ciudad ciudad = tuple.getT1();
                    final Administrador admin = tuple.getT2();

                    return conjuntoRepository.findAllByTelefono(request.telefono())
                            .collectList()
                            .flatMap(existentes -> {
                                if (!existentes.isEmpty()) {
                                    return this.<ConjuntoResidencial>errorFromMessage("domain.conjunto.telefono.duplicated");
                                }

                                return conjuntoRepository.findByCiudadAndNombre(ciudad.getId(), request.nombre())
                                        .flatMap(existing -> this.<ConjuntoResidencial>errorFromMessage("domain.conjunto.nombre.duplicated"))
                                        .switchIfEmpty(Mono.defer(() -> {
                                            final ConjuntoResidencial nuevo = mapper.toDomain(UUID.randomUUID(), request, ciudad,
                                                    admin);
                                            return conjuntoRepository.save(nuevo);
                                        }));
                            });
                })
                .map(mapper::toResponse)
                .flatMap(resp -> eventoPublisher.publish(new ConjuntoEvento("CREATED", resp)).thenReturn(resp))
                .onErrorResume(ApplicationException.class, Mono::error)
                .onErrorResume(Exception.class, ex -> {
                    Throwable cause = ex;
                    while (cause != null) {
                        if (cause instanceof ApplicationException) {
                            return Mono.error(cause);
                        }
                        cause = cause.getCause();
                    }
                    LOGGER.error("Error inesperado en CreateConjuntoUseCase: {}", ex.getMessage());
                    return this.<ConjuntoResponse>errorFromMessage("domain.general.error");
                });
    }

    private <T> Mono<T> errorFromMessage(final String messageKey) {
        return messageClient.getMessage(messageKey)
                .switchIfEmpty(messageClient.getMessage("domain.general.error"))
                .switchIfEmpty(Mono.error(new ApplicationException(
                        "No fue posible obtener el mensaje de error solicitado.", "message-service")))
                .flatMap(msg -> Mono.error(new ApplicationException(msg.clientMessage(), msg.source())));
    }
}
