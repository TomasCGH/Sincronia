package co.edu.uco.backendvictus.application.dto.pais;

import java.util.UUID;

public record PaisUpdateRequest(UUID id, String nombre) {
}
