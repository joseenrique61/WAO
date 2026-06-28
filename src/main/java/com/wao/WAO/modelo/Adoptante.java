package com.wao.WAO.modelo;

import java.util.Collection;

import javax.persistence.*;

import com.wao.WAO.validadores.ValidadorCedula;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.calculators.EnumCalculator;
import org.openxava.model.*;

import com.wao.WAO.modelo.enums.EstadoPerfil;

import lombok.*;

@Entity
@Getter
@Setter
public class Adoptante {
    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    String id;

    @Column(length = 20)
    @Required
    @PropertyValidator(ValidadorCedula.class)
    String dni;

    @Column(length = 100)
    @Required
    String nombre;

    @Column(length = 200)
    @Required
    String direccion;

    @Column(length = 20)
    @Required
    String telefono;

    @Column(length = 1000)
    @Required
    String detalleVivienda;

    boolean tieneNinos;

    boolean tieneMascotas;

    @OneToMany
    Collection<Entrevista> entrevistas;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @DefaultValueCalculator(
            value = EnumCalculator.class,
            properties = {
                    @PropertyValue(name = "enumType", value = "com.wao.WAO.modelo.enums.EstadoPerfil"),
                    @PropertyValue(name = "value", value = "PENDIENTE")
            }
    )
    EstadoPerfil estadoPerfil;
}
