package com.wao.WAO.modelo;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.Past;

import org.openxava.annotations.*;
import lombok.*;

import java.util.Date;

import com.wao.WAO.modelo.enums.*;

@Embeddable
@Getter
@Setter
public class SeguimientoPostAdopcion {
    @Required
    @Past
    Date fechaContacto;

    @Column(length = 1000)
    @Required
    String notasEstado;

    @Column(length = 30)
    @Required
    @Enumerated(EnumType.STRING)
    TipoContacto tipoContacto;

    @Future
    Date proximaFechaSeguimiento;
}
