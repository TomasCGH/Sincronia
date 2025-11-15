package co.edu.uco.backendvictus.application.mapper;

import org.springframework.stereotype.Component;

import co.edu.uco.backendvictus.application.dto.ciudad.CiudadPublicResponse;
import co.edu.uco.backendvictus.domain.model.Ciudad;

@Component
public class CiudadPublicApplicationMapper {

    public CiudadPublicResponse toPublicResponse(final Ciudad c) {
        if (c == null) {
            return null;
        }
        return new CiudadPublicResponse(c.getId(), c.getNombre(), c.getDepartamento() != null ? c.getDepartamento().getId() : null);
    }
}

