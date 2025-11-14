package co.edu.uco.backendvictus.application.usecase.conjunto;

import java.util.UUID;

import org.springframework.stereotype.Service;

import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoResponse;
import co.edu.uco.backendvictus.application.dto.common.PageResponse;
import co.edu.uco.backendvictus.application.mapper.ConjuntoApplicationMapper;
import co.edu.uco.backendvictus.application.port.out.conjunto.ConjuntoRepositoryPort;
import co.edu.uco.backendvictus.domain.model.conjunto.ConjuntoResidencial;

@Service
public class ListConjuntosUseCase {

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    private final ConjuntoRepositoryPort conjuntoRepository;
    private final ConjuntoApplicationMapper mapper;

    public ListConjuntosUseCase(final ConjuntoRepositoryPort conjuntoRepository,
            final ConjuntoApplicationMapper mapper) {
        this.conjuntoRepository = conjuntoRepository;
        this.mapper = mapper;
    }

    public Flux<ConjuntoResponse> execute() {
        return conjuntoRepository.findAllWithNames().map(mapper::toResponse);
    }

    public Flux<ConjuntoResponse> executeFiltered(final UUID departamentoId, final UUID ciudadId, final String nombre) {
        final boolean hasNombre = nombre != null && !nombre.isBlank();
        Flux<ConjuntoResidencial> flux;

        if (departamentoId == null && ciudadId == null) {
            flux = hasNombre ? conjuntoRepository.findByNombre(nombre) : conjuntoRepository.findAllWithNames();
        } else if (departamentoId != null && ciudadId == null) {
            flux = conjuntoRepository.findByDepartamentoId(departamentoId);
        } else if (departamentoId == null) {
            flux = conjuntoRepository.findByCiudadId(ciudadId);
        } else {
            flux = conjuntoRepository.findByDepartamentoIdAndCiudadId(departamentoId, ciudadId);
        }

        if (hasNombre && (departamentoId != null || ciudadId != null)) {
            final String normalized = nombre.toLowerCase();
            flux = flux.filter(conjunto -> conjunto.getNombre().toLowerCase().contains(normalized));
        }

        return flux.map(mapper::toResponse);
    }

    public Mono<PageResponse<ConjuntoResponse>> executePaged(final int page, final int size) {
        final int sanitizedPage = Math.max(page, 0);
        final int sanitizedSize = size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);

        return conjuntoRepository.countAll()
                .flatMap(total -> conjuntoRepository.findAllWithNamesPaged(sanitizedPage, sanitizedSize)
                        .map(mapper::toResponse)
                        .collectList()
                        .map(items -> PageResponse.of(items, total, sanitizedPage, sanitizedSize)));
    }

    public Mono<PageResponse<ConjuntoResponse>> buildFilteredPage(final Flux<ConjuntoResponse> filteredFlux,
            final int page, final int size) {
        final int sanitizedPage = Math.max(page, 0);
        final int sanitizedSize = size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);

        return filteredFlux.collectList()
                .map(items -> paginate(items, sanitizedPage, sanitizedSize));
    }

    private PageResponse<ConjuntoResponse> paginate(final List<ConjuntoResponse> items, final int page, final int size) {
        final int fromIndex = Math.min(page * size, items.size());
        final int toIndex = Math.min(fromIndex + size, items.size());
        final List<ConjuntoResponse> pageItems = items.subList(fromIndex, toIndex);
        return PageResponse.of(pageItems, items.size(), page, size);
    }
}
