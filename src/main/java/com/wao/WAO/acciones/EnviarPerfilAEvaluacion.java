package com.wao.WAO.acciones;

import org.openxava.actions.*;

import com.wao.WAO.modelo.*;

public class EnviarPerfilAEvaluacion extends ViewBaseAction {

    public void execute() throws Exception {
        Adoptante adoptante = (Adoptante) getView().getEntity();
//        adoptante.enviarPerfilAEvaluacion();
        addMessage("perfil_enviado_evaluacion");
    }
}
