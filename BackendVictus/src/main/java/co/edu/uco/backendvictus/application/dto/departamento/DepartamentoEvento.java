package co.edu.uco.backendvictus.application.dto.departamento;

import co.edu.uco.backendvictus.application.dto.evento.TipoEvento;

public record DepartamentoEvento(TipoEvento tipo, DepartamentoResponse payload) {
}
