package co.edu.uco.backendvictus.application.dto.vivienda;

import java.util.UUID;

public record ViviendaFilterRequest(UUID conjuntoId, String estado, String tipo, String numero, Integer page,
        Integer size) {
}
