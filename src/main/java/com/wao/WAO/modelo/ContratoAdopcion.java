package com.wao.WAO.modelo;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Past;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import lombok.*;

import java.util.Collection;
import java.util.Date;

import com.wao.WAO.modelo.enums.*;

@Entity
@Getter
@Setter
public class ContratoAdopcion {
    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @DescriptionsList
    @ReadOnly(onCreate = false)
    Adoptante adoptante;

    @ManyToOne(fetch = FetchType.LAZY)
    @DescriptionsList
    @ReadOnly(onCreate = false)
    Animal animal;

    @Required
    @Past
    @ReadOnly(onCreate = false)
    Date fechaAdopcion;

    @Column(length = 100)
    @Required
    @ReadOnly(onCreate = false)
    String responsableCentro;

    @ElementCollection
    @ListProperties("fechaContacto, notasEstado, tipoContacto")
    Collection<SeguimientoPostAdopcion> seguimientos;

    @AssertTrue(message = "La fecha de contacto no puede ser anterior a la fecha de adopción del animal")
    private boolean isFechaContactoValida() {
        for (SeguimientoPostAdopcion seguimientoPostAdopcion : seguimientos) {
            if (seguimientoPostAdopcion.getFechaContacto().before(fechaAdopcion)) {
                return false;
            }
        }

        return true;
    }

    public boolean validarCompatibilidad() {
        return adoptante != null && adoptante.getEstadoPerfil() == EstadoPerfil.APTO
                && animal != null && animal.getEstado() == EstadoAnimal.LISTO_PARA_ADOPCION;
    }

    public void procesarVinculacion() {
        if (!validarCompatibilidad()) {
            throw new IllegalArgumentException("El adoptante debe estar APTO y el animal LISTO_PARA_ADOPCION");
        }
        animal.cambiarEstado(EstadoAnimal.ADOPTADO, adoptante.getNombre());
    }

//    public boolean validarAnimalEstaAdoptado() {
//        return animal != null && animal.getEstado() == EstadoAnimal.ADOPTADO;
//    }

//    public void registrarSeguimiento(Date fechaContacto, String notasEstado, TipoContacto tipoContacto, Date proximaFecha) {
//        if (!validarAnimalEstaAdoptado()) {
//            throw new IllegalArgumentException("Solo se pueden registrar seguimientos para animales adoptados");
//        }
//        if (seguimientos == null) {
//            seguimientos = new ArrayList<>();
//        }
//        SeguimientoPostAdopcion seguimiento = new SeguimientoPostAdopcion();
//        seguimiento.setFechaContacto(fechaContacto);
//        seguimiento.setNotasEstado(notasEstado);
//        seguimiento.setTipoContacto(tipoContacto);
//        seguimiento.setProximaFechaSeguimiento(proximaFecha);
//        seguimientos.add(seguimiento);
//    }
}
