package co.edu.uco.backendvictus.application.dto.ciudad;

import java.util.UUID;

public record CiudadPublicResponse(UUID id, String nombre, UUID departamentoId) {
}

