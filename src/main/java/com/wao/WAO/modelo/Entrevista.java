package com.wao.WAO.modelo;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.jpa.*;

import com.wao.WAO.modelo.enums.ModalidadEntrevista;

import lombok.*;

@Entity
@Getter @Setter
public class Entrevista {

    @Id
    @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(length=32)
    String id;

    @ManyToOne(fetch=FetchType.LAZY)
    @DescriptionsList
    Adoptante adoptante;

    @Required
    LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(length=30)
    @Required
    ModalidadEntrevista modalidad;

    @Column(length=255)
    String enlaceReunion;

    @Column(length=100)
    String evaluador;

    public boolean validarDisponibilidadEvaluador(String evaluador, LocalDateTime fechaHora) {
        if (evaluador == null || fechaHora == null) return true;

        LocalDateTime inicio = fechaHora.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fin = inicio.plusHours(1);

        @SuppressWarnings("unchecked")
        List<Entrevista> conflictos = XPersistence.getManager()
            .createQuery(
                "SELECT e FROM Entrevista e WHERE e.evaluador = :evaluador " +
                "AND e.fechaHora >= :inicio AND e.fechaHora < :fin")
            .setParameter("evaluador", evaluador)
            .setParameter("inicio", inicio)
            .setParameter("fin", fin)
            .getResultList();

        return conflictos.isEmpty();
    }
}
