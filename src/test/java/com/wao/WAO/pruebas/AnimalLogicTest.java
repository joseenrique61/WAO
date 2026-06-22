package com.wao.WAO.pruebas;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

import com.wao.WAO.modelo.*;
import com.wao.WAO.modelo.enums.*;

public class AnimalLogicTest {

    private Animal animalConEstado(EstadoAnimal estado) {
        Animal a = new Animal();
        a.setEstado(estado);
        return a;
    }

    private TratamientoProfilactico profilaxis(TipoProfilactico tipo) {
        TratamientoProfilactico t = new TratamientoProfilactico();
        t.setTipo(tipo);
        t.setNombreProducto("X");
        t.setFechaAplicacion(new Date());
        return t;
    }

    private Collection<TratamientoProfilactico> profilaxisCompleta() {
        Collection<TratamientoProfilactico> tp = new ArrayList<>();
        tp.add(profilaxis(TipoProfilactico.VACUNA));
        tp.add(profilaxis(TipoProfilactico.DESPARASITANTE));
        return tp;
    }

    private EntradaClinica entrada(boolean contagiosa) {
        EntradaClinica e = new EntradaClinica();
        e.setEsContagiosa(contagiosa);
        return e;
    }

    // ---------- validarCondicionesParaAdopcion ----------

    @Test
    public void validarAdopcion_true_cuandoEnObservacionConProfilaxisCompletaYSinContagiosas() {
        Animal a = animalConEstado(EstadoAnimal.EN_OBSERVACION);
        a.setTratamientosProfilacticos(profilaxisCompleta());
        assertTrue(a.validarCondicionesParaAdopcion());
    }

    @Test
    public void validarAdopcion_false_siEstadoEsAdoptado() {
        Animal a = animalConEstado(EstadoAnimal.ADOPTADO);
        a.setTratamientosProfilacticos(profilaxisCompleta());
        assertFalse(a.validarCondicionesParaAdopcion());
    }

    @Test
    public void validarAdopcion_false_siEstadoEsFallecido() {
        Animal a = animalConEstado(EstadoAnimal.FALLECIDO);
        a.setTratamientosProfilacticos(profilaxisCompleta());
        assertFalse(a.validarCondicionesParaAdopcion());
    }

    @Test
    public void validarAdopcion_false_siHayAlgunaPatologiaContagiosa() {
        Animal a = animalConEstado(EstadoAnimal.EN_OBSERVACION);
        a.setTratamientosProfilacticos(profilaxisCompleta());
        Collection<EntradaClinica> ec = new ArrayList<>();
        ec.add(entrada(false));
        ec.add(entrada(true));
        a.setEntradasClinicas(ec);
        assertFalse(a.validarCondicionesParaAdopcion());
    }

    @Test
    public void validarAdopcion_true_siEntradasClinicasNoContagiosas() {
        Animal a = animalConEstado(EstadoAnimal.EN_OBSERVACION);
        a.setTratamientosProfilacticos(profilaxisCompleta());
        Collection<EntradaClinica> ec = new ArrayList<>();
        ec.add(entrada(false));
        ec.add(entrada(false));
        a.setEntradasClinicas(ec);
        assertTrue(a.validarCondicionesParaAdopcion());
    }

    @Test
    public void validarAdopcion_false_siFaltaVacuna() {
        Animal a = animalConEstado(EstadoAnimal.EN_OBSERVACION);
        Collection<TratamientoProfilactico> tp = new ArrayList<>();
        tp.add(profilaxis(TipoProfilactico.DESPARASITANTE));
        a.setTratamientosProfilacticos(tp);
        assertFalse(a.validarCondicionesParaAdopcion());
    }

    @Test
    public void validarAdopcion_false_siFaltaDesparasitante() {
        Animal a = animalConEstado(EstadoAnimal.EN_OBSERVACION);
        Collection<TratamientoProfilactico> tp = new ArrayList<>();
        tp.add(profilaxis(TipoProfilactico.VACUNA));
        a.setTratamientosProfilacticos(tp);
        assertFalse(a.validarCondicionesParaAdopcion());
    }

    @Test
    public void validarAdopcion_false_siSinTratamientos() {
        Animal a = animalConEstado(EstadoAnimal.EN_OBSERVACION);
        assertFalse(a.validarCondicionesParaAdopcion());
    }

    @Test
    public void validarAdopcion_false_siColeccionesNulas() {
        Animal a = animalConEstado(EstadoAnimal.EN_OBSERVACION);
        assertFalse(a.validarCondicionesParaAdopcion());
    }

    // ---------- validarTransicionEstado ----------

    @Test
    public void transicion_desdeNullPermiteCualquierEstado() {
        Animal a = animalConEstado(null);
        assertTrue(a.validarTransicionEstado(EstadoAnimal.RESCATADO));
        assertTrue(a.validarTransicionEstado(EstadoAnimal.EN_OBSERVACION));
        assertTrue(a.validarTransicionEstado(EstadoAnimal.FALLECIDO));
    }

    @Test
    public void transicion_desdeRescatado_soloPermiteEnObservacionOFallecido() {
        Animal a = animalConEstado(EstadoAnimal.RESCATADO);
        assertTrue(a.validarTransicionEstado(EstadoAnimal.EN_OBSERVACION));
        assertTrue(a.validarTransicionEstado(EstadoAnimal.FALLECIDO));
        assertFalse(a.validarTransicionEstado(EstadoAnimal.LISTO_PARA_ADOPCION));
        assertFalse(a.validarTransicionEstado(EstadoAnimal.ADOPTADO));
        assertFalse(a.validarTransicionEstado(EstadoAnimal.RESCATADO));
    }

    @Test
    public void transicion_desdeEnObservacion_permiteListoRescatadoOFallecido() {
        Animal a = animalConEstado(EstadoAnimal.EN_OBSERVACION);
        assertTrue(a.validarTransicionEstado(EstadoAnimal.LISTO_PARA_ADOPCION));
        assertTrue(a.validarTransicionEstado(EstadoAnimal.RESCATADO));
        assertTrue(a.validarTransicionEstado(EstadoAnimal.FALLECIDO));
        assertFalse(a.validarTransicionEstado(EstadoAnimal.ADOPTADO));
    }

    @Test
    public void transicion_desdeListoParaAdopcion_permiteAdoptadoEnObservacionOFallecido() {
        Animal a = animalConEstado(EstadoAnimal.LISTO_PARA_ADOPCION);
        assertTrue(a.validarTransicionEstado(EstadoAnimal.ADOPTADO));
        assertTrue(a.validarTransicionEstado(EstadoAnimal.EN_OBSERVACION));
        assertTrue(a.validarTransicionEstado(EstadoAnimal.FALLECIDO));
        assertFalse(a.validarTransicionEstado(EstadoAnimal.RESCATADO));
        assertFalse(a.validarTransicionEstado(EstadoAnimal.LISTO_PARA_ADOPCION));
    }

    @Test
    public void transicion_desdeAdoptado_bloqueada() {
        Animal a = animalConEstado(EstadoAnimal.ADOPTADO);
        assertFalse(a.validarTransicionEstado(EstadoAnimal.EN_OBSERVACION));
        assertFalse(a.validarTransicionEstado(EstadoAnimal.RESCATADO));
        assertFalse(a.validarTransicionEstado(EstadoAnimal.FALLECIDO));
        assertFalse(a.validarTransicionEstado(EstadoAnimal.LISTO_PARA_ADOPCION));
    }

    @Test
    public void transicion_desdeFallecido_bloqueada() {
        Animal a = animalConEstado(EstadoAnimal.FALLECIDO);
        assertFalse(a.validarTransicionEstado(EstadoAnimal.ADOPTADO));
        assertFalse(a.validarTransicionEstado(EstadoAnimal.RESCATADO));
        assertFalse(a.validarTransicionEstado(EstadoAnimal.EN_OBSERVACION));
        assertFalse(a.validarTransicionEstado(EstadoAnimal.LISTO_PARA_ADOPCION));
    }

    // ---------- cambiarEstado ----------

    @Test
    public void cambiarEstado_actualizaEstadoYAgregaLog() {
        Animal a = animalConEstado(EstadoAnimal.RESCATADO);
        a.cambiarEstado(EstadoAnimal.EN_OBSERVACION, "vet1");

        assertEquals(EstadoAnimal.EN_OBSERVACION, a.getEstado());
        assertNotNull(a.getLogsEstado());
        assertEquals(1, a.getLogsEstado().size());

        LogEstadoAnimal log = a.getLogsEstado().iterator().next();
        assertEquals(EstadoAnimal.EN_OBSERVACION, log.getNuevoEstado());
        assertEquals("vet1", log.getUsuarioAsigno());
        assertNotNull(log.getFechaCambio());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cambiarEstado_transicionInvalidaLanzaExcepcion() {
        Animal a = animalConEstado(EstadoAnimal.RESCATADO);
        a.cambiarEstado(EstadoAnimal.ADOPTADO, "vet1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void cambiarEstado_desdeAdoptadoLanzaExcepcion() {
        Animal a = animalConEstado(EstadoAnimal.ADOPTADO);
        a.cambiarEstado(EstadoAnimal.EN_OBSERVACION, "vet1");
    }

    @Test
    public void cambiarEstado_acumulaMultiplesLogsEnOrden() {
        Animal a = animalConEstado(null);
        a.cambiarEstado(EstadoAnimal.RESCATADO, "u1");
        a.cambiarEstado(EstadoAnimal.EN_OBSERVACION, "u2");
        a.cambiarEstado(EstadoAnimal.LISTO_PARA_ADOPCION, "u3");

        assertEquals(3, a.getLogsEstado().size());
        Iterator<LogEstadoAnimal> it = a.getLogsEstado().iterator();
        assertEquals("u1", it.next().getUsuarioAsigno());
        assertEquals("u2", it.next().getUsuarioAsigno());
        assertEquals("u3", it.next().getUsuarioAsigno());
    }

    @Test
    public void cambiarEstado_inicializaColeccionDeLogsSiEsNull() {
        Animal a = animalConEstado(null);
        assertNull(a.getLogsEstado());
        a.cambiarEstado(EstadoAnimal.RESCATADO, "u1");
        assertNotNull(a.getLogsEstado());
        assertEquals(1, a.getLogsEstado().size());
    }

    @Test
    public void cambiarEstado_noModificaEstadoSiLaTransicionEsInvalida() {
        Animal a = animalConEstado(EstadoAnimal.RESCATADO);
        try {
            a.cambiarEstado(EstadoAnimal.ADOPTADO, "u1");
            fail("Deberia lanzar excepcion");
        } catch (IllegalArgumentException expected) {
            assertEquals(EstadoAnimal.RESCATADO, a.getEstado());
            assertNull(a.getLogsEstado());
        }
    }

    // ---------- establecerListoParaAdopcion ----------

    @Test
    public void establecerListo_cambiaEstadoYAgregaLogCuandoCumpleCondiciones() {
        Animal a = animalConEstado(EstadoAnimal.EN_OBSERVACION);
        a.setTratamientosProfilacticos(profilaxisCompleta());

        a.establecerListoParaAdopcion("admin");

        assertEquals(EstadoAnimal.LISTO_PARA_ADOPCION, a.getEstado());
        assertEquals(1, a.getLogsEstado().size());
        LogEstadoAnimal log = a.getLogsEstado().iterator().next();
        assertEquals(EstadoAnimal.LISTO_PARA_ADOPCION, log.getNuevoEstado());
        assertEquals("admin", log.getUsuarioAsigno());
    }

    @Test
    public void establecerListo_noCambiaEstadoSiFaltaProfilaxis() {
        Animal a = animalConEstado(EstadoAnimal.EN_OBSERVACION);

        a.establecerListoParaAdopcion("admin");

        assertEquals(EstadoAnimal.EN_OBSERVACION, a.getEstado());
        assertNull(a.getLogsEstado());
    }

    @Test
    public void establecerListo_noCambiaEstadoSiHayPatologiaContagiosa() {
        Animal a = animalConEstado(EstadoAnimal.EN_OBSERVACION);
        a.setTratamientosProfilacticos(profilaxisCompleta());
        Collection<EntradaClinica> ec = new ArrayList<>();
        ec.add(entrada(true));
        a.setEntradasClinicas(ec);

        a.establecerListoParaAdopcion("admin");

        assertEquals(EstadoAnimal.EN_OBSERVACION, a.getEstado());
        assertNull(a.getLogsEstado());
    }

    @Test
    public void establecerListo_noCambiaEstadoSiAnimalYaFallecido() {
        Animal a = animalConEstado(EstadoAnimal.FALLECIDO);
        a.setTratamientosProfilacticos(profilaxisCompleta());

        a.establecerListoParaAdopcion("admin");

        assertEquals(EstadoAnimal.FALLECIDO, a.getEstado());
        assertNull(a.getLogsEstado());
    }

    // ---------- adoptarAnimal ----------

    @Test
    public void adoptarAnimal_desdeListo_cambiaEstadoYRegistraAdoptante() {
        Animal a = animalConEstado(EstadoAnimal.LISTO_PARA_ADOPCION);

        a.adoptarAnimal("juan");

        assertEquals(EstadoAnimal.ADOPTADO, a.getEstado());
        assertEquals(1, a.getLogsEstado().size());
        LogEstadoAnimal log = a.getLogsEstado().iterator().next();
        assertEquals(EstadoAnimal.ADOPTADO, log.getNuevoEstado());
        assertEquals("juan", log.getUsuarioAsigno());
    }

    @Test(expected = IllegalArgumentException.class)
    public void adoptarAnimal_desdeEnObservacionLanzaExcepcion() {
        Animal a = animalConEstado(EstadoAnimal.EN_OBSERVACION);
        a.adoptarAnimal("juan");
    }

    @Test
    public void adoptarAnimal_flujoCompletoRescatadoAAdoptado() {
        Animal a = animalConEstado(null);
        a.cambiarEstado(EstadoAnimal.RESCATADO, "rescatista");
        a.cambiarEstado(EstadoAnimal.EN_OBSERVACION, "vet");
        a.cambiarEstado(EstadoAnimal.LISTO_PARA_ADOPCION, "coordinador");
        a.adoptarAnimal("familiaPerez");

        assertEquals(EstadoAnimal.ADOPTADO, a.getEstado());
        assertEquals(4, a.getLogsEstado().size());
    }
}