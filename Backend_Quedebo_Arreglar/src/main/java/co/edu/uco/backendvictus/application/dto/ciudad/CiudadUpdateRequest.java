package co.edu.uco.backendvictus.application.dto.ciudad;

import java.util.UUID;

public record CiudadUpdateRequest(UUID id, UUID departamentoId, String nombre) {
}
