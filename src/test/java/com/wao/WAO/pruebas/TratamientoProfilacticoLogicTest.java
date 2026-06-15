package com.wao.WAO.pruebas;

import org.openxava.tests.*;

public class TratamientoProfilacticoLogicTest extends ModuleTestBase {

    public TratamientoProfilacticoLogicTest(String testName) {
        super(testName, "WAO", "TratamientoProfilactico");
    }

    public void testFechaPasadaValida() throws Exception {
        login("admin", "admin");
    }

    public void testFechaFuturaInvalida() throws Exception {
        login("admin", "admin");
    }

    public void testRecordatorioRefuerzo() throws Exception {
        login("admin", "admin");
    }
}
