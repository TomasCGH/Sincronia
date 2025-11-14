package co.edu.uco.backendvictus.application.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.edu.uco.backendvictus.application.dto.ciudad.CiudadCreateRequest;
import co.edu.uco.backendvictus.application.dto.ciudad.CiudadResponse;
import co.edu.uco.backendvictus.application.dto.ciudad.CiudadUpdateRequest;
import co.edu.uco.backendvictus.domain.model.Ciudad;
import co.edu.uco.backendvictus.domain.model.Departamento;

@Mapper(componentModel = "spring")
public abstract class CiudadApplicationMapper {

    public Ciudad toDomain(final UUID id, final CiudadCreateRequest request, final Departamento departamento) {
        return Ciudad.create(id, request.nombre(), departamento);
    }

    public Ciudad toDomain(final CiudadUpdateRequest request, final Departamento departamento) {
        return Ciudad.create(request.id(), request.nombre(), departamento);
    }

    @Mapping(target = "departamentoId", source = "departamento.id")
    public abstract CiudadResponse toResponse(Ciudad ciudad);
}
