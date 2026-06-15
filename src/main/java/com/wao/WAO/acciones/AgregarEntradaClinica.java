package com.wao.WAO.acciones;

import org.openxava.actions.*;
import com.wao.WAO.modelo.*;

public class AgregarEntradaClinica extends ViewBaseAction {

    public void execute() throws Exception {
        EntradaClinica entradaClinica = (EntradaClinica) getView().getEntity();
        try {
            entradaClinica.registrarEntrada();
            if (entradaClinica.isEsContagiosa()) {
                addMessage("alerta_patologia_contagiosa");
            }
            getView().refresh();
        } catch (IllegalStateException e) {
            addError(e.getMessage());
        }
    }
}
