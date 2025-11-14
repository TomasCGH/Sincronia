package co.edu.uco.backendvictus.infrastructure.secondary.mapper;

import org.springframework.stereotype.Component;

import co.edu.uco.backendvictus.domain.model.conjunto.ConjuntoResidencial;
import co.edu.uco.backendvictus.domain.model.Vivienda;
import co.edu.uco.backendvictus.domain.model.ViviendaEstado;
import co.edu.uco.backendvictus.domain.model.ViviendaTipo;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.ViviendaEntity;

@Component
public class ViviendaEntityMapper {

    public ViviendaEntity toEntity(final Vivienda vivienda) {
        return new ViviendaEntity(vivienda.getId(), vivienda.getNumero(), vivienda.getTipo().getValue(),
                vivienda.getEstado().getValue(), vivienda.getConjunto().getId());
    }

    public Vivienda toDomain(final ViviendaEntity entity, final ConjuntoResidencial conjunto) {
        if (entity == null) {
            return null;
        }
        final ViviendaTipo tipo = ViviendaTipo.from(entity.getTipo());
        final ViviendaEstado estado = ViviendaEstado.from(entity.getEstado());
        return Vivienda.create(entity.getId(), entity.getNumero(), tipo, estado, conjunto);
    }
}
