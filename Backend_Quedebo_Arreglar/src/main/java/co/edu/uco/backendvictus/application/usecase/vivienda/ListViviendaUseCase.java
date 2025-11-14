package co.edu.uco.backendvictus.application.usecase.vivienda;

import java.util.Optional;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaFilterRequest;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaPageResponse;
import co.edu.uco.backendvictus.application.mapper.ViviendaApplicationMapper;
import co.edu.uco.backendvictus.domain.model.ViviendaEstado;
import co.edu.uco.backendvictus.domain.model.ViviendaTipo;
import co.edu.uco.backendvictus.domain.port.ViviendaRepository;

@Service
public class ListViviendaUseCase {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    private final ViviendaRepository viviendaRepository;
    private final ViviendaApplicationMapper mapper;

    public ListViviendaUseCase(final ViviendaRepository viviendaRepository,
            final ViviendaApplicationMapper mapper) {
        this.viviendaRepository = viviendaRepository;
        this.mapper = mapper;
    }

    public Mono<ViviendaPageResponse> execute(final ViviendaFilterRequest request) {
        final int page = Optional.ofNullable(request.page()).filter(p -> p >= 0).orElse(DEFAULT_PAGE);
        final int size = Optional.ofNullable(request.size()).filter(s -> s > 0).orElse(DEFAULT_SIZE);
        final String numero = Optional.ofNullable(request.numero()).map(String::trim)
                .filter(value -> !value.isBlank()).orElse(null);
        final ViviendaEstado estado = Optional.ofNullable(request.estado())
                .filter(value -> !value.isBlank())
                .map(ViviendaEstado::from)
                .orElse(null);
        final ViviendaTipo tipo = Optional.ofNullable(request.tipo())
                .filter(value -> !value.isBlank())
                .map(ViviendaTipo::from)
                .orElse(null);

        return viviendaRepository.countByFilters(request.conjuntoId(), estado, tipo, numero)
                .flatMap(total -> viviendaRepository.findByFilters(request.conjuntoId(), estado, tipo, numero, page, size)
                        .map(mapper::toResponse)
                        .collectList()
                        .map(items -> new ViviendaPageResponse(items, total, page, size)));
    }
}
