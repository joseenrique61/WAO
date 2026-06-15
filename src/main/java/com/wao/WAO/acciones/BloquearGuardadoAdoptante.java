package com.wao.WAO.acciones;

import java.util.Collection;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.validators.*;

import com.wao.WAO.modelo.*;
import com.wao.WAO.modelo.enums.*;

public class BloquearGuardadoAdoptante extends SaveAction {
    @Override
    public void execute() throws Exception {
        EstadoPerfil estado = (EstadoPerfil) getView().getValue("estadoPerfil");
        if (estado == EstadoPerfil.APTO) {
            @SuppressWarnings("unchecked")
            Collection<Entrevista> entrevistas = (Collection<Entrevista>) getView().getValue("entrevistas");
            if (entrevistas == null || entrevistas.isEmpty()) {
                addError("adoptante_sin_entrevistas");
                return;
            }
        }
        super.execute();
    }
}
