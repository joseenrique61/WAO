package com.wao.WAO.pruebas;

import org.openxava.tests.*;

public class ContratoAdopcionLogicTest extends ModuleTestBase {

    public ContratoAdopcionLogicTest(String testName) {
        super(testName, "WAO", "ContratoAdopcion");
    }

    public void testCompatibilidadAptoYListo() throws Exception {
        login("admin", "admin");
    }

    public void testCompatibilidadNoApto() throws Exception {
        login("admin", "admin");
    }

    public void testCompatibilidadPendiente() throws Exception {
        login("admin", "admin");
    }

    public void testCompatibilidadAnimalNoListo() throws Exception {
        login("admin", "admin");
    }

    public void testVinculacionCambiaEstadoAAdoptado() throws Exception {
        login("admin", "admin");
    }

    public void testSeguimientoSoloAdoptados() throws Exception {
        login("admin", "admin");
    }
}
