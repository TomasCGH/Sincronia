package co.edu.uco.backendvictus.application.usecase.vivienda;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaResponse;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaUpdateRequest;
import co.edu.uco.backendvictus.application.mapper.ViviendaApplicationMapper;
import co.edu.uco.backendvictus.application.port.out.conjunto.ConjuntoRepositoryPort;
import co.edu.uco.backendvictus.application.usecase.UseCase;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.model.Vivienda;
import co.edu.uco.backendvictus.domain.model.conjunto.ConjuntoResidencial;
import co.edu.uco.backendvictus.domain.port.ViviendaRepository;

@Service
public class UpdateViviendaUseCase implements UseCase<ViviendaUpdateRequest, ViviendaResponse> {

    private final ViviendaRepository viviendaRepository;
    private final ConjuntoRepositoryPort conjuntoRepository;
    private final ViviendaApplicationMapper mapper;

    public UpdateViviendaUseCase(final ViviendaRepository viviendaRepository,
            final ConjuntoRepositoryPort conjuntoRepository, final ViviendaApplicationMapper mapper) {
        this.viviendaRepository = viviendaRepository;
        this.conjuntoRepository = conjuntoRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<ViviendaResponse> execute(final ViviendaUpdateRequest request) {
        return viviendaRepository.findById(request.id())
                .switchIfEmpty(Mono.error(new ApplicationException("Vivienda no encontrada")))
                .flatMap(actual -> conjuntoRepository.findById(request.conjuntoId())
                        .switchIfEmpty(Mono.error(new ApplicationException("Conjunto residencial no encontrado")))
                        .flatMap(conjunto -> validateUnicidad(actual, request, conjunto)
                                .flatMap(viviendaRepository::save)))
                .map(mapper::toResponse);
    }

    private Mono<Vivienda> validateUnicidad(final Vivienda actual, final ViviendaUpdateRequest request,
            final ConjuntoResidencial conjunto) {
        final Vivienda viviendaActualizada = mapper.toDomain(actual, request, conjunto);
        return viviendaRepository.findByConjuntoAndNumero(conjunto.getId(), viviendaActualizada.getNumero())
                .filter(encontrada -> !encontrada.getId().equals(actual.getId()))
                .flatMap(encontrada -> Mono.<Vivienda>error(
                        new ApplicationException("Ya existe una vivienda con ese numero en el conjunto")))
                .switchIfEmpty(Mono.just(viviendaActualizada));
    }
}
