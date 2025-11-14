package co.edu.uco.backendvictus.application.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;

import co.edu.uco.backendvictus.application.dto.administrador.AdministradorCreateRequest;
import co.edu.uco.backendvictus.application.dto.administrador.AdministradorResponse;
import co.edu.uco.backendvictus.application.dto.administrador.AdministradorUpdateRequest;
import co.edu.uco.backendvictus.domain.model.Administrador;

@Mapper(componentModel = "spring")
public abstract class AdministradorApplicationMapper {

    public Administrador toDomain(final UUID id, final AdministradorCreateRequest request) {
        return Administrador.create(id, request.primerNombre(), request.segundoNombres(), request.primerApellido(),
                request.segundoApellido(), request.email(), request.telefono());
    }

    public Administrador toDomain(final AdministradorUpdateRequest request) {
        return Administrador.create(request.id(), request.primerNombre(), request.segundoNombres(),
                request.primerApellido(), request.segundoApellido(), request.email(), request.telefono());
    }

    public abstract AdministradorResponse toResponse(Administrador administrador);
}
