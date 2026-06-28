package com.wao.WAO.acciones;

import com.wao.WAO.modelo.enums.EstadoAnimal;
import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.validators.*;

import com.wao.WAO.modelo.*;

import java.util.Date;

public class RegistrarDefuncion extends SaveAction {
    public void execute() throws Exception {
        Defuncion defuncion = (Defuncion) getView().getEntity();

        if (defuncion.getAnimal() == null) {
            addError("El animal es obligatorio.");
            return;
        }

        if (defuncion.getAnimal().getEstado() == EstadoAnimal.FALLECIDO) {
            addError("Este animal ya tiene un registro de fallecimiento.");
            return;
        }

        try {
            defuncion.getAnimal().registrarDefuncion(defuncion.getFechaDefuncion());
            addMessage("Defunción registrada para " + defuncion.getAnimal().getNombre());
        } catch (Exception e) {
            addError("Error al registrar la defunción: " + e.getMessage());
        }

        super.execute();
    }
}
