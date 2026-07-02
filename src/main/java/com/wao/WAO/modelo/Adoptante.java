package com.wao.WAO.modelo;

import java.util.Collection;

import javax.persistence.*;

import com.wao.WAO.validadores.ValidadorAdoptante;
import com.wao.WAO.validadores.ValidadorCedula;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.calculators.EnumCalculator;

import com.wao.WAO.modelo.enums.EstadoPerfil;

import lombok.*;

@Entity
@Getter
@Setter
@EntityValidator(value = ValidadorAdoptante.class,
        properties = {
                @PropertyValue(name = "estadoPerfil"),
                @PropertyValue(name = "entrevistas")
        }
)
public class Adoptante {
    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    String id;

    @Column(length = 10)
    @Mask("0000000000")
    @Required
    @PropertyValidator(ValidadorCedula.class)
    String dni;

    @Column(length = 100)
    @Required
    String nombre;

    @Column(length = 200)
    @Required
    String direccion;

    @Column(length = 12)
    @Mask("000 000 0000")
    @Required
    String telefonoMovil;

    @Column(length = 1000)
    @Required
    String detalleVivienda;

    boolean tieneNinos;

    boolean tieneMascotas;

    @OneToMany(mappedBy = "adoptante", cascade = CascadeType.ALL, orphanRemoval = true)
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
