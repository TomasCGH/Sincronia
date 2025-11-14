package co.edu.uco.backendvictus.infrastructure.secondary.mapper;

import org.springframework.stereotype.Component;

import co.edu.uco.backendvictus.domain.model.Administrador;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.AdministradorEntity;

@Component
public class AdministradorEntityMapper {

    public AdministradorEntity toEntity(final Administrador administrador) {
        return new AdministradorEntity(administrador.getId(), administrador.getPrimerNombre(),
                administrador.getSegundoNombres(), administrador.getPrimerApellido(),
                administrador.getSegundoApellido(), administrador.getEmail(), administrador.getTelefono());
    }

    public Administrador toDomain(final AdministradorEntity entity) {
        if (entity == null) {
            return null;
        }
        return Administrador.create(entity.getId(), entity.getPrimerNombre(), entity.getSegundoNombre(),
                entity.getPrimerApellido(), entity.getSegundoApellido(), entity.getCorreo(), entity.getTelefono());
    }
}
