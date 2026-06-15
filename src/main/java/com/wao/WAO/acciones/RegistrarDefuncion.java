package com.wao.WAO.acciones;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.validators.*;

import com.wao.WAO.modelo.*;

import java.util.Date;

public class RegistrarDefuncion extends ViewBaseAction {

    public void execute() throws Exception {
        Animal animal = (Animal) MapFacade.findEntity(getView().getModelName(), getView().getKeyValues());
        String causa = getView().getValueString("causaFallecimiento");
        Date fecha = (Date) getView().getValue("fechaDefuncion");
        String notas = getView().getValueString("notasVeterinario");
        if (fecha == null) {
            fecha = new Date();
        }
        try {
//            animal.registrarDefuncion(causa, fecha, notas);
            MapFacade.setValues(getView().getModelName(), getView().getKeyValues(), getView().getValues());
            addMessage("defuncion_registrada", animal.getNombre());
        } catch (Exception e) {
            addError("error_registrar_defuncion", e.getMessage());
        }
    }
}
