package com.wao.WAO.acciones;

import com.wao.WAO.modelo.Animal;
import org.openxava.actions.ViewBaseAction;

public class EstablecerListoParaAdopcion extends ViewBaseAction {
    public void execute() throws Exception {
        Animal animal = (Animal) getView().getEntity();
        if (animal.validarCondicionesParaAdopcion()) {
            animal.establecerListoParaAdopcion(getView().getValueString("usuario"));
            addMessage("animal_cumple_requisitos");
        } else {
            addError("animal_no_cumple_requisitos");
        }
    }
}
