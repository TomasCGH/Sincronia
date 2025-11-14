package co.edu.uco.backendvictus.application.dto.vivienda;

import java.util.List;

public record ViviendaPageResponse(List<ViviendaResponse> items, long total, int page, int size) {
}
