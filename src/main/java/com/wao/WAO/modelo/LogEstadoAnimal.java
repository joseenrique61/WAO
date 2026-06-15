package com.wao.WAO.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

import java.util.Date;

import com.wao.WAO.modelo.enums.*;

@Embeddable
@Getter @Setter
public class LogEstadoAnimal {

    @Required
    Date fechaCambio;

    @Enumerated(EnumType.STRING)
    @Column(length=30)
    @Required
    EstadoAnimal nuevoEstado;

    @Column(length=100)
    String usuarioAsigno;
}
