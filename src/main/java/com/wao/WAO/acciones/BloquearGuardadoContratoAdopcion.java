package com.wao.WAO.acciones;

import java.util.Map;

import org.openxava.actions.*;
import org.openxava.model.*;

import com.wao.WAO.modelo.*;
import com.wao.WAO.modelo.enums.*;

public class BloquearGuardadoContratoAdopcion extends SaveAction {

    @Override
    public void execute() throws Exception {
        Map adoptanteKey = getView().getSubview("adoptante").getKeyValues();
        Map animalKey = getView().getSubview("animal").getKeyValues();
        if (adoptanteKey == null || adoptanteKey.isEmpty() || animalKey == null || animalKey.isEmpty()) {
            addError("error_vinculacion", "Debe seleccionar adoptante y animal");
            return;
        }
        Adoptante adoptante = (Adoptante) MapFacade.findEntity("Adoptante", adoptanteKey);
        Animal animal = (Animal) MapFacade.findEntity("Animal", animalKey);
        try {
            ContratoAdopcion contrato = new ContratoAdopcion();
            contrato.procesarVinculacion(adoptante, animal);
        } catch (IllegalArgumentException e) {
            addError("error_vinculacion", e.getMessage());
            return;
        }
        String usuario = getView().getValueString("responsableCentro");
        if (usuario == null || usuario.isEmpty()) {
            getView().setValue("responsableCentro", "admin");
        }
        super.execute();
    }
}
