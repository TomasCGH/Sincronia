package co.edu.uco.backendvictus.application.dto.departamento;

import java.util.UUID;

public record DepartamentoUpdateRequest(UUID id, UUID paisId, String nombre) {
}
