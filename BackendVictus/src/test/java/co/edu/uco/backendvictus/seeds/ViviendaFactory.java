package co.edu.uco.backendvictus.seeds;

import java.util.UUID;

import co.edu.uco.backendvictus.domain.model.Administrador;
import co.edu.uco.backendvictus.domain.model.Ciudad;
import co.edu.uco.backendvictus.domain.model.conjunto.ConjuntoResidencial;
import co.edu.uco.backendvictus.domain.model.Departamento;
import co.edu.uco.backendvictus.domain.model.Pais;
import co.edu.uco.backendvictus.domain.model.Vivienda;
import co.edu.uco.backendvictus.domain.model.ViviendaEstado;
import co.edu.uco.backendvictus.domain.model.ViviendaTipo;

public final class ViviendaFactory {

    private ViviendaFactory() {
    }

    public static ConjuntoResidencial buildConjunto() {
        final Pais pais = Pais.create(UUID.randomUUID(), "Colombia");
        final Departamento departamento = Departamento.create(UUID.randomUUID(), "Antioquia", pais);
        final Ciudad ciudad = Ciudad.create(UUID.randomUUID(), "Medellin", departamento);
        final Administrador administrador = Administrador.create(UUID.randomUUID(), "Ana", null, "Lopez", null,
                "ana@uco.edu", "1234567");
        return ConjuntoResidencial.create(UUID.randomUUID(), "Conjunto Central", "Cra 10 #20", ciudad,
                administrador, "3000000");
    }

    public static Vivienda buildVivienda(final ConjuntoResidencial conjunto, final String numero) {
        return Vivienda.create(UUID.randomUUID(), numero, ViviendaTipo.APARTAMENTO, ViviendaEstado.DISPONIBLE,
                conjunto);
    }
}
