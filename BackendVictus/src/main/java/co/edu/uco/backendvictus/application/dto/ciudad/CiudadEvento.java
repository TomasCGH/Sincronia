package co.edu.uco.backendvictus.application.dto.ciudad;

import co.edu.uco.backendvictus.application.dto.evento.TipoEvento;

public record CiudadEvento(TipoEvento tipo, CiudadResponse payload) {
}
