package co.edu.uco.backendvictus.application.mapper;

import org.springframework.stereotype.Component;
import co.edu.uco.backendvictus.domain.model.Administrador;
import co.edu.uco.backendvictus.application.dto.administrador.AdministradorCatalogResponse;

/**
 * Mapper aislado para construir el DTO de catálogo sin afectar el mapper principal.
 */
@Component
public class AdministradorCatalogMapper {

    public AdministradorCatalogResponse toCatalog(final Administrador administrador) {
        if (administrador == null) return null;
        final StringBuilder nombreBuilder = new StringBuilder();
        nombreBuilder.append(nullSafe(administrador.getPrimerNombre()));
        nombreBuilder.append(' ');
        nombreBuilder.append(nullSafe(administrador.getSegundoNombres()));
        nombreBuilder.append(' ');
        nombreBuilder.append(nullSafe(administrador.getPrimerApellido()));
        nombreBuilder.append(' ');
        nombreBuilder.append(nullSafe(administrador.getSegundoApellido()));
        final String nombreCompleto = nombreBuilder.toString().replaceAll(" +", " ").trim();
        return new AdministradorCatalogResponse(
                administrador.getId(),
                nombreCompleto,
                administrador.getEmail(),
                administrador.getTelefono()
        );
    }

    private String nullSafe(final String value) {
        return value == null ? "" : value.trim();
    }
}
