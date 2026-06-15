package com.wao.WAO.acciones;

import java.util.Map;

import org.openxava.actions.*;
import org.openxava.model.*;

import com.wao.WAO.modelo.*;

public class ProcesarVinculacion extends ViewBaseAction {

    public void execute() throws Exception {
        ContratoAdopcion contrato = (ContratoAdopcion) getView().getEntity();
        Map adoptanteKey = getView().getSubview("adoptante").getKeyValues();
        Map animalKey = getView().getSubview("animal").getKeyValues();
        if (adoptanteKey == null || adoptanteKey.isEmpty() || animalKey == null || animalKey.isEmpty()) {
            addError("error_vinculacion", "Debe seleccionar adoptante y animal");
            return;
        }
        Adoptante adoptante = (Adoptante) MapFacade.findEntity("Adoptante", adoptanteKey);
        Animal animal = (Animal) MapFacade.findEntity("Animal", animalKey);
        try {
            contrato.procesarVinculacion(adoptante, animal);
            getView().refresh();
            addMessage("vinculacion_exitosa", animal.getNombre(), adoptante.getNombre());
        } catch (Exception e) {
            addError("error_vinculacion", e.getMessage());
        }
    }
}
