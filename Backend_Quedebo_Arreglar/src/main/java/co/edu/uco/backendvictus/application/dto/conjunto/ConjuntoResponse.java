package co.edu.uco.backendvictus.application.dto.conjunto;

import java.util.UUID;

public record ConjuntoResponse(UUID id, UUID ciudadId, UUID administradorId, String nombre, String direccion,
        String telefono, String nombreCiudad, String nombreDepartamento) {
}
