package com.wao.WAO.acciones;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.validators.*;

import com.wao.WAO.modelo.*;
import com.wao.WAO.modelo.enums.*;

public class CambiarEstadoAnimal extends ViewBaseAction {

    public void execute() throws Exception {
        Animal animal = (Animal) MapFacade.findEntity(getView().getModelName(), getView().getKeyValues());
        EstadoAnimal estado = (EstadoAnimal) getView().getValue("estado");
        String usuario = getView().getValueString("usuarioAsigno");
        if (usuario == null || usuario.isEmpty()) {
            usuario = "admin";
        }
        try {
            animal.cambiarEstado(estado, usuario);
            MapFacade.setValues(getView().getModelName(), getView().getKeyValues(), getView().getValues());
            addMessage("estado_cambiado", animal.getNombre(), estado.name());
        } catch (IllegalArgumentException e) {
            addError("transicion_no_valida", e.getMessage());
        }
    }
}
