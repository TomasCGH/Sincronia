package co.edu.uco.backendvictus.infrastructure.secondary.mapper;

import org.springframework.stereotype.Component;

import co.edu.uco.backendvictus.domain.model.Administrador;
import co.edu.uco.backendvictus.domain.model.Ciudad;
import co.edu.uco.backendvictus.domain.model.conjunto.ConjuntoResidencial;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.ConjuntoResidencialEntity;

@Component
public class ConjuntoResidencialEntityMapper {

    public ConjuntoResidencialEntity toEntity(final ConjuntoResidencial conjuntoResidencial) {
        // Por defecto, marcamos como nueva (modo creación)
        return toEntity(conjuntoResidencial, true);
    }

    public ConjuntoResidencialEntity toEntity(final ConjuntoResidencial conjuntoResidencial, final boolean isNew) {
        return new ConjuntoResidencialEntity(
                conjuntoResidencial.getId(),
                conjuntoResidencial.getNombre(),
                conjuntoResidencial.getDireccion(),
                conjuntoResidencial.getCiudad().getId(),
                conjuntoResidencial.getAdministrador().getId(),
                conjuntoResidencial.getTelefono(),
                isNew
        );
    }

    public ConjuntoResidencial toDomain(final ConjuntoResidencialEntity entity, final Ciudad ciudad,
            final Administrador administrador) {
        if (entity == null) {
            return null;
        }
        return ConjuntoResidencial.create(entity.getId(), entity.getNombre(), entity.getDireccion(), ciudad,
                administrador, entity.getTelefono());
    }
}
