package co.edu.uco.backendvictus.application.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaChangeEstadoRequest;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaCreateRequest;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaResponse;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaUpdateRequest;
import co.edu.uco.backendvictus.domain.model.conjunto.ConjuntoResidencial;
import co.edu.uco.backendvictus.domain.model.Vivienda;
import co.edu.uco.backendvictus.domain.model.ViviendaEstado;
import co.edu.uco.backendvictus.domain.model.ViviendaTipo;

@Mapper(componentModel = "spring")
public abstract class ViviendaApplicationMapper {

    public Vivienda toDomain(final UUID id, final ViviendaCreateRequest request, final ConjuntoResidencial conjunto) {
        final ViviendaTipo tipo = ViviendaTipo.from(request.tipo());
        final ViviendaEstado estado = request.estado() == null ? ViviendaEstado.DISPONIBLE
                : ViviendaEstado.from(request.estado());
        return Vivienda.create(id, request.numero(), tipo, estado, conjunto);
    }

    public Vivienda toDomain(final Vivienda existing, final ViviendaUpdateRequest request,
            final ConjuntoResidencial conjunto) {
        final ViviendaTipo tipo = ViviendaTipo.from(request.tipo());
        final ViviendaEstado estado = ViviendaEstado.from(request.estado());
        return existing.update(request.numero(), tipo, estado, conjunto);
    }

    public Vivienda applyEstadoChange(final Vivienda vivienda, final ViviendaChangeEstadoRequest request) {
        final ViviendaEstado estado = ViviendaEstado.from(request.estado());
        return vivienda.changeEstado(estado);
    }

    @Mapping(target = "conjuntoId", source = "conjunto.id")
    @Mapping(target = "tipo", expression = "java(vivienda.getTipo().getValue())")
    @Mapping(target = "estado", expression = "java(vivienda.getEstado().getValue())")
    public abstract ViviendaResponse toResponse(Vivienda vivienda);
}
