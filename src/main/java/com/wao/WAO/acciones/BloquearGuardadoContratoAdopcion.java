package com.wao.WAO.acciones;

import org.openxava.actions.*;
import org.openxava.model.*;

import com.wao.WAO.modelo.*;
import com.wao.WAO.modelo.enums.*;

public class BloquearGuardadoContratoAdopcion extends SaveAction {

    @Override
    public void execute() throws Exception {
        ContratoAdopcion contratoAdopcion = (ContratoAdopcion) getView().getEntity();

        if (contratoAdopcion.getId() != null && !contratoAdopcion.getId().isEmpty()) {
            super.execute();
            return;
        }

        try {
            contratoAdopcion.procesarVinculacion();
        } catch (IllegalArgumentException e) {
            addError("error_vinculacion", e.getMessage());
            return;
        }

        super.execute();
    }
}
