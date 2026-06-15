package com.wao.WAO.acciones;

import org.openxava.actions.*;
import com.wao.WAO.modelo.*;

public class RegistrarTratamiento extends ViewBaseAction {

    public void execute() throws Exception {
        TratamientoProfilactico tratamiento = (TratamientoProfilactico) getView().getEntity();
        if (!tratamiento.validarFechaAplicacionPasada()) {
            addError("fecha_aplicacion_futura");
            return;
        }
        getView().refresh();
        if (tratamiento.agendarRecordatorioRefuerzo()) {
            addMessage("recordatorio_refuerzo_programado");
        }
    }
}
