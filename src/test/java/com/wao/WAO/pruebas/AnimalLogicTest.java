package com.wao.WAO.pruebas;

import org.openxava.tests.*;

public class AnimalLogicTest extends ModuleTestBase {

    public AnimalLogicTest(String testName) {
        super(testName, "WAO", "Animal");
    }

    public void testTransicionRescatadoAEnObservacion() throws Exception {
        login("admin", "admin");
    }

    public void testTransicionRescatadoAAdoptado() throws Exception {
        login("admin", "admin");
    }

    public void testValidarCondicionesParaAdopcionSinPatologias() throws Exception {
        login("admin", "admin");
    }

    public void testValidarCondicionesParaAdopcionConPatologiaActiva() throws Exception {
        login("admin", "admin");
    }
}
