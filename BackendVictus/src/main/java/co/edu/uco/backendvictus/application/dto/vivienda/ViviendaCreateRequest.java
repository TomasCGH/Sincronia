package co.edu.uco.backendvictus.application.dto.vivienda;

import java.util.UUID;

public record ViviendaCreateRequest(UUID conjuntoId, String numero, String tipo, String estado) {
}
