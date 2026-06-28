package com.wao.WAO.modelo;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.Past;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import lombok.*;

import java.util.Date;

import com.wao.WAO.modelo.enums.*;

@Entity
@Getter
@Setter
public class TratamientoProfilactico {
    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @DescriptionsList
    Animal animal;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    @Required
    TipoProfilactico tipo;

    @Column(length = 100)
    @Required
    String nombreProducto;

    @Required
    @Past
    Date fechaAplicacion;

    @Future
    Date fechaProximoRefuerzo;

    @AssertTrue(message = "La fecha de aplicación no puede ser anterior a la fecha de rescate del animal")
    private boolean isFechaAplicacionValida() {
        if (fechaAplicacion == null || animal == null || animal.getFechaRescate() == null) {
            return true;
        }

        return !fechaAplicacion.before(animal.getFechaRescate());
    }
}
