package co.edu.uco.backendvictus.application.dto.ciudad;

import java.util.UUID;

public record CiudadCreateRequest(UUID departamentoId, String nombre) {
}
