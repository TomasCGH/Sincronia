package co.edu.uco.backendvictus.application.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;

import co.edu.uco.backendvictus.application.dto.pais.PaisCreateRequest;
import co.edu.uco.backendvictus.application.dto.pais.PaisResponse;
import co.edu.uco.backendvictus.application.dto.pais.PaisUpdateRequest;
import co.edu.uco.backendvictus.domain.model.Pais;

@Mapper(componentModel = "spring")
public abstract class PaisApplicationMapper {

    public Pais toDomain(final UUID id, final PaisCreateRequest request) {
        return Pais.create(id, request.nombre());
    }

    public Pais toDomain(final PaisUpdateRequest request) {
        return Pais.create(request.id(), request.nombre());
    }

    public abstract PaisResponse toResponse(Pais pais);
}
