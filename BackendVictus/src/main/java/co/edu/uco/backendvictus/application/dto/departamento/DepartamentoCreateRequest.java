package co.edu.uco.backendvictus.application.dto.departamento;

import java.util.UUID;

public record DepartamentoCreateRequest(UUID paisId, String nombre) {
}
