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
            addError("El animal es obligatorio.");
            return;
        }

        if (defuncion.getAnimal().getEstado() == EstadoAnimal.FALLECIDO) {
            addError("Este animal ya tiene un registro de fallecimiento.");
            return;
        }

        List<Date> fechas = new ArrayList<>(defuncion.getAnimal().getEntradasClinicas().stream().map(EntradaClinica::getFechaConsulta).toList());
        fechas.addAll(defuncion.getAnimal().getTratamientosProfilacticos().stream().map(TratamientoProfilactico::getFechaAplicacion).toList());
        fechas.addAll(defuncion.getAnimal().getLogsEstado().stream().map(LogEstadoAnimal::getFechaCambio).toList());
        fechas.add(defuncion.getAnimal().getFechaRescate());

        Date ultimaFecha = Collections.max(fechas);

        if (defuncion.getFechaDefuncion().before(ultimaFecha)) {
            addError("La fecha de defunción no puede ser anterior a la fecha de rescate del animal, su último tratamiento profiláctico, última entrada clínica o último cambio de estado.");
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
