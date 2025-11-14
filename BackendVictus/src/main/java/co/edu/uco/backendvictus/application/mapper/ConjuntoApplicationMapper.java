package co.edu.uco.backendvictus.application.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoCreateRequest;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoResponse;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoUpdateRequest;
import co.edu.uco.backendvictus.domain.model.Administrador;
import co.edu.uco.backendvictus.domain.model.Ciudad;
import co.edu.uco.backendvictus.domain.model.conjunto.ConjuntoResidencial;

@Mapper(componentModel = "spring")
public abstract class ConjuntoApplicationMapper {

    public ConjuntoResidencial toDomain(final UUID id, final ConjuntoCreateRequest request, final Ciudad ciudad,
            final Administrador administrador) {
        return ConjuntoResidencial.create(id, request.nombre(), request.direccion(), ciudad, administrador,
                request.telefono());
    }

    public ConjuntoResidencial toDomain(final ConjuntoUpdateRequest request, final Ciudad ciudad,
            final Administrador administrador) {
        return ConjuntoResidencial.create(request.id(), request.nombre(), request.direccion(), ciudad, administrador,
                request.telefono());
    }

    @Mapping(target = "ciudadId", source = "ciudad.id")
    @Mapping(target = "administradorId", source = "administrador.id")
    @Mapping(target = "nombreCiudad", source = "ciudad.nombre")
    @Mapping(target = "nombreDepartamento", source = "ciudad.departamento.nombre")
    public abstract ConjuntoResponse toResponse(ConjuntoResidencial conjuntoResidencial);
}
