package com.wao.WAO.modelo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.Required;
import org.openxava.annotations.View;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Past;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@View(name = "Simple", members = "fechaDefuncion; causaFallecimiento")
public class Defuncion {
    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    String id;

    @Required
    @Past
    Date fechaDefuncion;

    @Required
    @Column(length = 200)
    String causaFallecimiento;

    @OneToOne(optional = false)
    @ReferenceView("Simple")
    @Required
    Animal animal;

//    @AssertTrue(message = "La fecha de defunción no puede ser anterior a la fecha de rescate del animal, su último tratamiento profiláctico, última entrada clínica o último cambio de estado.")
//    private boolean isFechaDefuncionValida() {
//        List<Date> fechas = new ArrayList<>(animal.entradasClinicas.stream().map(EntradaClinica::getFechaConsulta).toList());
//        fechas.addAll(animal.tratamientosProfilacticos.stream().map(TratamientoProfilactico::getFechaAplicacion).toList());
//        fechas.addAll(animal.logsEstado.stream().map(LogEstadoAnimal::getFechaCambio).toList());
//        fechas.add(animal.getFechaRescate());
//
//        Date ultimaFecha = Collections.max(fechas);
//
//        return !fechaDefuncion.before(ultimaFecha);
//    }
}
