package com.wao.WAO.pruebas;

import org.openxava.tests.*;

public class EntrevistaLogicTest extends ModuleTestBase {

    public EntrevistaLogicTest(String testName) {
        super(testName, "WAO", "Entrevista");
    }

    public void testAdoptanteCompletoPuedeAgendar() throws Exception {
        login("admin", "admin");
    }

    public void testAdoptanteIncompletoNoPuedeAgendar() throws Exception {
        login("admin", "admin");
    }

    public void testHorarioLibreValido() throws Exception {
        login("admin", "admin");
    }

    public void testHorarioSolapadoInvalido() throws Exception {
        login("admin", "admin");
    }
}
