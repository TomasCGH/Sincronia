package co.edu.uco.backendvictus.application.dto.conjunto;

import co.edu.uco.backendvictus.application.dto.evento.TipoEvento;

public record ConjuntoEvento(TipoEvento tipo, ConjuntoResponse payload) {
}
