package co.edu.uco.backendvictus.application.dto.administrador;

public record AdministradorCreateRequest(String primerNombre, String segundoNombres, String primerApellido,
        String segundoApellido, String email, String telefono) {
}
