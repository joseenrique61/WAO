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

    @AssertTrue(message = "La fecha de adopción no puede ser anterior a la fecha de rescate del animal o la fecha en la que pasó a estar apto para ser adoptado.")
    private boolean isFechaAdopcionValida() {
        Date fechaListoParaAdopcion = animal.getLogsEstado().stream().filter(a -> a.nuevoEstado == EstadoAnimal.LISTO_PARA_ADOPCION).toList().get(0).getFechaCambio();
        return !fechaAdopcion.before(animal.getFechaRescate()) && !fechaAdopcion.before(fechaListoParaAdopcion);
    }

    @AssertTrue(message = "La fecha de contacto no puede ser anterior a la fecha de adopción del animal")
    private boolean isFechaContactoValida() {
        if (seguimientos == null) {
            return true;
        }

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
        animal.adoptarAnimal(responsableCentro, fechaAdopcion);
    }
}
