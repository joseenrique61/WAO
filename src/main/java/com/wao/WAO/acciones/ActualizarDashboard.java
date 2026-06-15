package com.wao.WAO.acciones;

import org.openxava.actions.*;

public class ActualizarDashboard extends ViewBaseAction {

    public void execute() throws Exception {
        getView().refresh();
        addMessage("dashboard_actualizado");
    }
}
