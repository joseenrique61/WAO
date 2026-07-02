package com.wao.WAO.modelo;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Past;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import lombok.*;

import java.util.Date;

import com.wao.WAO.modelo.enums.*;

@Entity
@Getter
@Setter
public class EntradaClinica {
    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @DescriptionsList
    Animal animal;

    @Required
    @Past
    Date fechaConsulta;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    @Required
    TipoEntradaClinica tipo;

    @Column(length = 1000)
    @Required
    String diagnostico;

    @Column(length = 100)
    String veterinarioTratante;

    boolean esContagiosa;

    @AssertTrue(message = "fecha_consulta_no_anterior_a_rescate")
    private boolean isFechaConsultaValida() {
        if (fechaConsulta == null || animal == null || animal.getFechaRescate() == null) {
            return true;
        }

        return !fechaConsulta.before(animal.getFechaRescate());
    }
}
