package com.wao.WAO.acciones;

import com.wao.WAO.modelo.enums.EstadoAnimal;
import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.validators.*;

import com.wao.WAO.modelo.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RegistrarDefuncion extends SaveAction {
    public void execute() throws Exception {
        Defuncion defuncion = (Defuncion) getView().getEntity();

        if (defuncion.getAnimal() == null) {
            addError("defuncion_animal_obligatorio");
            return;
        }

        if (defuncion.getAnimal().getEstado() == EstadoAnimal.FALLECIDO) {
            addError("defuncion_ya_registrada");
            return;
        }

        List<Date> fechas = new ArrayList<>(defuncion.getAnimal().getEntradasClinicas().stream().map(EntradaClinica::getFechaConsulta).toList());
        fechas.addAll(defuncion.getAnimal().getTratamientosProfilacticos().stream().map(TratamientoProfilactico::getFechaAplicacion).toList());
        fechas.addAll(defuncion.getAnimal().getLogsEstado().stream().map(LogEstadoAnimal::getFechaCambio).toList());
        fechas.add(defuncion.getAnimal().getFechaRescate());

        Date ultimaFecha = Collections.max(fechas);

        if (defuncion.getFechaDefuncion().before(ultimaFecha)) {
            addError("defuncion_fecha_invalida");
            return;
        }

        try {
            defuncion.getAnimal().registrarDefuncion(defuncion.getResponsableCentro(), defuncion.getFechaDefuncion());
            addMessage("defuncion_registrada", defuncion.getAnimal().getNombre());
        } catch (Exception e) {
            addError("defuncion_error_registro", e.getMessage());
        }

        super.execute();
    }
}
