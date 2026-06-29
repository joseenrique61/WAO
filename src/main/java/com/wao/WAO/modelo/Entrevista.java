package com.wao.WAO.modelo;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;
import javax.validation.ValidationException;

import com.wao.WAO.modelo.enums.EstadoPerfil;
import com.wao.WAO.validadores.ValidadorEntrevista;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.jpa.*;

import com.wao.WAO.modelo.enums.ModalidadEntrevista;

import lombok.*;

@Entity
@Getter
@Setter
@EntityValidator(value = ValidadorEntrevista.class,
        properties = {
                @PropertyValue(name = "id", from = "id"),
                @PropertyValue(name = "adoptante", from = "adoptante"),
                @PropertyValue(name = "fechaHora", from = "fechaHora"),
                @PropertyValue(name = "evaluador", from = "evaluador"),
        }
)
public class Entrevista {
    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @DescriptionsList
    Adoptante adoptante;

    @Required
    LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    @Required
    ModalidadEntrevista modalidad;

    @Column(length = 255)
    String enlaceReunion;

    @Column(length = 100)
    @Required
    String evaluador;
}
