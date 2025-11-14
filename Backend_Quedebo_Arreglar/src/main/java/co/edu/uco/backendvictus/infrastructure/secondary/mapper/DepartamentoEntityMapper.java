package co.edu.uco.backendvictus.infrastructure.secondary.mapper;

import org.springframework.stereotype.Component;

import co.edu.uco.backendvictus.domain.model.Departamento;
import co.edu.uco.backendvictus.domain.model.Pais;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.DepartamentoEntity;

@Component
public class DepartamentoEntityMapper {

    public DepartamentoEntity toEntity(final Departamento departamento) {
        return new DepartamentoEntity(departamento.getId(), departamento.getPais().getId(), departamento.getNombre());
    }

    public Departamento toDomain(final DepartamentoEntity entity, final Pais pais) {
        if (entity == null) {
            return null;
        }
        return Departamento.create(entity.getId(), entity.getNombre(), pais);
    }
}
