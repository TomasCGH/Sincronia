package co.edu.uco.backendvictus.application.dto.conjunto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ConjuntoUpdateRequest(
		@NotNull
		UUID id,
		@NotNull(message = "validation.required.ciudad")
		UUID ciudadId,
		@NotNull(message = "validation.required.administrador")
		UUID administradorId,
		@NotBlank(message = "validation.required.nombre")
		@Size(min = 3, message = "validation.minlength.nombre")
		String nombre,
		String direccion,
		@NotBlank(message = "validation.required.telefono")
		@Pattern(regexp = "^[0-9]+$", message = "validation.format.telefono")
		@Size(max = 10, message = "validation.maxlength.telefono")
		String telefono) {
}
