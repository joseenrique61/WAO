package com.wao.WAO.pruebas;

import org.openxava.tests.*;

public class AdoptanteLogicTest extends ModuleTestBase {

    public AdoptanteLogicTest(String testName) {
        super(testName, "WAO", "Adoptante");
    }

    public void testCamposCompletosValidos() throws Exception {
        login("admin", "admin");
    }

    public void testCamposIncompletosInvalidos() throws Exception {
        login("admin", "admin");
    }

    public void testCalificarApto() throws Exception {
        login("admin", "admin");
    }

    public void testCalificarNoApto() throws Exception {
        login("admin", "admin");
    }
}
