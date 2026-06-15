package com.wao.WAO.pruebas;

import org.openxava.tests.*;

public class SedeTest extends ModuleTestBase {

    public SedeTest(String testName) {
        super(testName, "WAO", "Sede");
    }

    public void testCalcularOcupacionActual() throws Exception {
        login("admin", "admin");
    }

    public void testDesactivarSedeConAnimalesActivos() throws Exception {
        login("admin", "admin");
    }
}
