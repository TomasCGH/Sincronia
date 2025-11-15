package co.edu.uco.backendvictus.application.dto.administrador;

import java.util.UUID;

/**
 * DTO mínimo para catálogo de administradores.
 */
public record AdministradorCatalogResponse(
        UUID id,
        String nombreCompleto,
        String email,
        String telefono
) {}
