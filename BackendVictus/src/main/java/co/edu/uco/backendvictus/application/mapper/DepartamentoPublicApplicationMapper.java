package co.edu.uco.backendvictus.application.mapper;

import org.springframework.stereotype.Component;

import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoPublicResponse;
import co.edu.uco.backendvictus.domain.model.Departamento;

@Component
public class DepartamentoPublicApplicationMapper {

    public DepartamentoPublicResponse toPublicResponse(final Departamento d) {
        if (d == null) {
            return null;
        }
        return new DepartamentoPublicResponse(d.getId(), d.getNombre(), d.getPais() != null ? d.getPais().getId() : null);
    }
}

