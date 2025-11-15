package co.edu.uco.backendvictus.application.dto.departamento;

import java.util.UUID;

public record DepartamentoPublicResponse(UUID id, String nombre, UUID paisId) {
}

