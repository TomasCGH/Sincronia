package co.edu.uco.backendvictus.application.dto.conjunto;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ConjuntoCreateRequest(
        @NotNull(message = "validation.required.ciudad")
        UUID ciudadId,
        @NotNull(message = "validation.required.administrador")
        UUID administradorId,
        @NotBlank(message = "validation.required.nombre")
        @Size(min = 3, max = 100, message = "validation.size.nombre")
        String nombre,
        @NotBlank(message = "validation.required.direccion")
        @Size(min = 3, max = 150, message = "validation.size.direccion")
        String direccion,
        @NotBlank(message = "validation.required.telefono")
        @Pattern(regexp = "^[0-9]+$", message = "validation.format.telefono")
        @Size(min = 7, max = 10, message = "validation.length.telefono")
        String telefono) {
}
