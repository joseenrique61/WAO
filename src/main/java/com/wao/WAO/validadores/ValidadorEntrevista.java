package com.wao.WAO.validadores;

import com.wao.WAO.modelo.Adoptante;
import com.wao.WAO.modelo.Entrevista;
import com.wao.WAO.modelo.enums.EstadoPerfil;
import lombok.Getter;
import lombok.Setter;
import org.openxava.jpa.XPersistence;
import org.openxava.util.Messages;
import org.openxava.validators.IValidator;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ValidadorEntrevista implements IValidator {
    String id;
    Adoptante adoptante;
    LocalDateTime fechaHora;
    String evaluador;

    @Override
    public void validate(Messages errors) throws Exception {
        if (adoptante == null) {
            errors.add("La entrevista requiere de un adoptante.");
        }

        if (adoptante.getEstadoPerfil() != EstadoPerfil.PENDIENTE) {
            errors.add("Solo se puede agendar una entrevista a un adoptante con estado Pendiente.");
        }

        if (!validarEvaluadorDisponible()) {
            errors.add("El evaluador no está disponible en la hora seleccionada.");
        }
    }

    private boolean validarEvaluadorDisponible() {
        @SuppressWarnings("unchecked")
        List<Entrevista> conflictos = XPersistence.getManager()
                .createQuery(
                        "SELECT e FROM Entrevista e WHERE e.id <> :id AND e.evaluador = :evaluador")
                .setParameter("id", id)
                .setParameter("evaluador", evaluador)
                .getResultList();

        for (Entrevista e : conflictos) {
            LocalDateTime fin = fechaHora.plusHours(1);

            LocalDateTime inicioConflicto = e.getFechaHora();
            LocalDateTime finConflicto = e.getFechaHora().plusHours(1);

            if ((inicioConflicto.isBefore(fechaHora) || inicioConflicto.isEqual(fechaHora)) &&
                    (finConflicto.isAfter(fechaHora) || finConflicto.isEqual(fechaHora))
                    || (inicioConflicto.isBefore(fin) || inicioConflicto.isEqual(fin)) &&
                    (finConflicto.isAfter(fin) || finConflicto.isEqual(fin))
            ) {
                return false;
            }
        }

        return true;
    }
}
