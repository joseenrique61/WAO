package com.wao.WAO.acciones;

import java.time.LocalDateTime;

import org.openxava.actions.*;
import org.openxava.jpa.*;

import com.wao.WAO.modelo.*;

public class AgendarEntrevista extends ViewBaseAction {

    public void execute() throws Exception {
        Adoptante adoptante = (Adoptante) getView().getEntity();
        Entrevista entrevista = new Entrevista();
        entrevista.setAdoptante(adoptante);

        String evaluador = getView().getValueString("evaluador");
        LocalDateTime fechaHora = (LocalDateTime) getView().getValue("fechaHora");

        if (!entrevista.validarDisponibilidadEvaluador(evaluador, fechaHora)) {
            addError("evaluador_no_disponible");
            return;
        }

        entrevista.setEvaluador(evaluador);
        entrevista.setFechaHora(fechaHora);
        entrevista.setModalidad(
            java.util.Optional.ofNullable(getView().getValueString("modalidad"))
                .map(com.wao.WAO.modelo.enums.ModalidadEntrevista::valueOf)
                .orElse(null)
        );
        entrevista.setEnlaceReunion(getView().getValueString("enlaceReunion"));

        XPersistence.getManager().persist(entrevista);
        addMessage("entrevista_agendada_exito");
    }
}
