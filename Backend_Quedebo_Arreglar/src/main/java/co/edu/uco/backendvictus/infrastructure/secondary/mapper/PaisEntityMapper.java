package co.edu.uco.backendvictus.infrastructure.secondary.mapper;

import org.springframework.stereotype.Component;

import co.edu.uco.backendvictus.domain.model.Pais;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.PaisEntity;

@Component
public class PaisEntityMapper {

    public PaisEntity toEntity(final Pais pais) {
        return new PaisEntity(pais.getId(), pais.getNombre());
    }

    public Pais toDomain(final PaisEntity entity) {
        if (entity == null) {
            return null;
        }
        return Pais.create(entity.getId(), entity.getNombre());
    }
}
