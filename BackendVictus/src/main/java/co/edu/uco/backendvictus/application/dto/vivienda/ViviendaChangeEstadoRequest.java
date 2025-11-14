package co.edu.uco.backendvictus.application.dto.vivienda;

import java.util.UUID;

public record ViviendaChangeEstadoRequest(UUID id, String estado) {
}
