package co.edu.uco.backendvictus.application.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoCreateRequest;
import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoResponse;
import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoUpdateRequest;
import co.edu.uco.backendvictus.domain.model.Departamento;
import co.edu.uco.backendvictus.domain.model.Pais;

@Mapper(componentModel = "spring")
public abstract class DepartamentoApplicationMapper {

    public Departamento toDomain(final UUID id, final DepartamentoCreateRequest request, final Pais pais) {
        return Departamento.create(id, request.nombre(), pais);
    }

    public Departamento toDomain(final DepartamentoUpdateRequest request, final Pais pais) {
        return Departamento.create(request.id(), request.nombre(), pais);
    }

    @Mapping(target = "paisId", source = "pais.id")
    public abstract DepartamentoResponse toResponse(Departamento departamento);
}
