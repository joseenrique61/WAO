package com.wao.WAO.acciones;

import org.openxava.actions.*;
import com.wao.WAO.modelo.*;

public class DesactivarSede extends ViewBaseAction {

    public void execute() throws Exception {
        Sede sede = (Sede) getView().getEntity();
        boolean resultado = sede.desactivarSede();
        if (resultado) {
            addMessage("sede_desactivada_exito");
        } else {
            addError("sede_no_se_puede_desactivar");
        }
    }
}
