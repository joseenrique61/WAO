package com.wao.WAO.pruebas;

import org.openxava.tests.*;

public class CuadroMandoLogicTest extends ModuleTestBase {

    public CuadroMandoLogicTest(String testName) {
        super(testName, "WAO", "CuadroMando");
    }

    public void testCalcularMetricasPoblacionTotal() throws Exception {
        login("admin", "admin");
    }

    public void testPorcentajeOcupacion() throws Exception {
        login("admin", "admin");
    }

    public void testPorcentajeExitoAdopciones() throws Exception {
        login("admin", "admin");
    }

    public void testFiltroPorSede() throws Exception {
        login("admin", "admin");
    }
}
