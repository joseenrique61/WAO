package com.wao.WAO.modelo;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.Past;

import org.hibernate.annotations.Parent;
import org.openxava.annotations.*;
import lombok.*;

import java.util.Date;

import com.wao.WAO.modelo.enums.*;

@Embeddable
@Getter
@Setter
public class SeguimientoPostAdopcion {
//    @ManyToOne
//    @DescriptionsList(descriptionProperties = "adoptante.nombre, animal.nombre")
//    ContratoAdopcion contratoAdopcion;


    @Transient
    private ContratoAdopcion contratoAdopcion; // Referencia automática al objeto que la contiene

//    // Es importante ocultarlo en la vista para no crear un bucle infinito
//    @Hidden
//    public ContratoAdopcion getContratoAdopcion() {
//        return contratoAdopcion;
//    }

    @Required
    @Past
    Date fechaContacto;

    @Column(length = 1000)
    String notasEstado;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    TipoContacto tipoContacto;

    @Future
    Date proximaFechaSeguimiento;

}
