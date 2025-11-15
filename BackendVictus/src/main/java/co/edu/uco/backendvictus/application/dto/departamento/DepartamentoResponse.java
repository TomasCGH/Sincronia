package co.edu.uco.backendvictus.application.dto.departamento;

import java.util.UUID;

public record DepartamentoResponse(UUID departamentoId,
        String departamentoNombre,
        UUID paisId) {
}
