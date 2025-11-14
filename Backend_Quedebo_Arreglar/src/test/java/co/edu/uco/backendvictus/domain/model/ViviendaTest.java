package co.edu.uco.backendvictus.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.edu.uco.backendvictus.crosscutting.exception.DomainException;
import co.edu.uco.backendvictus.seeds.ViviendaFactory;

class ViviendaTest {

    @Test
    void shouldNormalizeNumeroToUpperCase() {
        final ConjuntoResidencial conjunto = ViviendaFactory.buildConjunto();
        final Vivienda vivienda = Vivienda.create(UUID.randomUUID(), "a-101", ViviendaTipo.CASA,
                ViviendaEstado.DISPONIBLE, conjunto);

        assertEquals("A-101", vivienda.getNumero());
    }

    @Test
    void shouldFailWhenTipoIsInvalid() {
        assertThrows(DomainException.class, () -> ViviendaTipo.from("Penthouse"));
    }

    @Test
    void shouldFailWhenEstadoIsInvalid() {
        assertThrows(DomainException.class, () -> ViviendaEstado.from("Fuera de servicio"));
    }

    @Test
    void shouldFailWhenConjuntoMissing() {
        assertThrows(DomainException.class, () -> Vivienda.create(UUID.randomUUID(), "101", ViviendaTipo.CASA,
                ViviendaEstado.DISPONIBLE, null));
    }
}
