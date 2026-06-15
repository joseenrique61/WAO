package com.wao.WAO.pruebas;

import org.openxava.tests.*;

public class EntradaClinicaLogicTest extends ModuleTestBase {

    public EntradaClinicaLogicTest(String testName) {
        super(testName, "WAO", "EntradaClinica");
    }

    public void testAlertaPatologiaContagiosa() throws Exception {
        login("admin", "admin");
    }

    public void testRegistroEntradaClinica() throws Exception {
        login("admin", "admin");
    }
}
