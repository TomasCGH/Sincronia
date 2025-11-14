package co.edu.uco.backendvictus.application.usecase.vivienda;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaCreateRequest;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaResponse;
import co.edu.uco.backendvictus.application.mapper.ViviendaApplicationMapper;
import co.edu.uco.backendvictus.application.port.out.conjunto.ConjuntoRepositoryPort;
import co.edu.uco.backendvictus.application.usecase.UseCase;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.crosscutting.helpers.LoggerHelper;
import co.edu.uco.backendvictus.domain.model.Vivienda;
import co.edu.uco.backendvictus.domain.model.conjunto.ConjuntoResidencial;
import co.edu.uco.backendvictus.domain.port.ViviendaRepository;
import co.edu.uco.backendvictus.infrastructure.secondary.client.MessageClient;
import co.edu.uco.backendvictus.infrastructure.secondary.client.ParameterClient;

@Service
public class CreateViviendaUseCase implements UseCase<ViviendaCreateRequest, ViviendaResponse> {

    private static final Logger LOGGER = LoggerHelper.getLogger(CreateViviendaUseCase.class);

    private final ViviendaRepository viviendaRepository;
    private final ConjuntoRepositoryPort conjuntoRepository;
    private final ViviendaApplicationMapper mapper;
    private final MessageClient messageClient;
    private final ParameterClient parameterClient;

    public CreateViviendaUseCase(final ViviendaRepository viviendaRepository,
                                 final ConjuntoRepositoryPort conjuntoRepository,
                                 final ViviendaApplicationMapper mapper) {
        this(viviendaRepository, conjuntoRepository, mapper, MessageClient.fallback(), ParameterClient.fallback());
    }

    @Autowired
    public CreateViviendaUseCase(final ViviendaRepository viviendaRepository,
            final ConjuntoRepositoryPort conjuntoRepository,
            final ViviendaApplicationMapper mapper,
            final MessageClient messageClient,
            final ParameterClient parameterClient) {
        this.viviendaRepository = viviendaRepository;
        this.conjuntoRepository = conjuntoRepository;
        this.mapper = mapper;
        this.messageClient = messageClient;
        this.parameterClient = parameterClient;
    }

    @Override
    public Mono<ViviendaResponse> execute(final ViviendaCreateRequest request) {
        return parameterClient.get("vivienda.max.limit")
                .doOnNext(p -> LOGGER.info("ParameterService → parámetro 'vivienda.max.limit' = {} (source={})", p.value(), p.source()))
                .then(
                        conjuntoRepository.findById(request.conjuntoId())
                                .switchIfEmpty(Mono.error(new ApplicationException("Conjunto residencial no encontrado", "backend")))
                                .flatMap(conjunto -> buildAndValidate(request, conjunto)
                                        .flatMap(viviendaRepository::save))
                                .map(mapper::toResponse)
                );
    }

    private Mono<Vivienda> buildAndValidate(final ViviendaCreateRequest request,
            final ConjuntoResidencial conjunto) {
        final Vivienda vivienda = mapper.toDomain(null, request, conjunto);
        return viviendaRepository.findByConjuntoAndNumero(conjunto.getId(), vivienda.getNumero())
                .flatMap(existing -> messageClient.getMessage("domain.vivienda.numero.duplicated")
                        .switchIfEmpty(Mono.just(new MessageClient.MessageResult(
                                "Duplicate housing detected.",
                                "Ya existe una vivienda con ese número en el conjunto.",
                                "backend-default")))
                        .flatMap(msg -> {
                            LOGGER.warn("Creación de vivienda duplicada detectada. Technical='{}'", msg.technicalMessage());
                            return Mono.<Vivienda>error(new ApplicationException(msg.clientMessage(), msg.source()));
                        })
                )
                .switchIfEmpty(Mono.just(vivienda));
    }
}
