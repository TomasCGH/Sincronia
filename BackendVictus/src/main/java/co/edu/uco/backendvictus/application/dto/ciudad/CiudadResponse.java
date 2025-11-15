package co.edu.uco.backendvictus.application.dto.ciudad;

import java.util.UUID;

public record CiudadResponse(UUID ciudadId,
        String ciudadNombre,
        UUID departamentoId,
        String departamentoNombre) {
}
