package com.wao.WAO.modelo;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import lombok.*;

import java.util.Date;

import com.wao.WAO.modelo.enums.*;

@Entity
@Getter @Setter
public class TratamientoProfilactico {

    @Id
    @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(length=32)
    String id;

    @ManyToOne(fetch=FetchType.LAZY)
    @DescriptionsList
    Animal animal;

    @Enumerated(EnumType.STRING)
    @Column(length=30)
    @Required
    TipoProfilactico tipo;

    @Column(length=100)
    @Required
    String nombreProducto;

    @Required
    Date fechaAplicacion;

    Date fechaProximoRefuerzo;

    public boolean validarFechaAplicacionPasada() {
        Date today = new Date();
        return !fechaAplicacion.after(today);
    }

    public boolean agendarRecordatorioRefuerzo() {
        if (fechaProximoRefuerzo == null) {
            return false;
        }
        Date today = new Date();
        return fechaProximoRefuerzo.after(today);
    }
}
