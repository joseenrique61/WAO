package com.wao.WAO.acciones;

import org.openxava.actions.*;

import com.wao.WAO.modelo.*;
import com.wao.WAO.modelo.enums.*;

public class CalificarPerfil extends ViewBaseAction {

    public void execute() throws Exception {
        Adoptante adoptante = (Adoptante) getView().getEntity();
        String calificacionStr = getView().getValueString("estadoPerfil");
        EstadoPerfil calificacion = EstadoPerfil.valueOf(calificacionStr);
//        adoptante.calificarPerfil(calificacion);
        addMessage("perfil_calificado", calificacionStr);
    }
}
