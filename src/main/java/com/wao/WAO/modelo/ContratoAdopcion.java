package com.wao.WAO.modelo;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Past;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.util.XavaResources;
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

    @AssertTrue(message = "fecha_adopcion_no_anterior_a_rescate_o_listo")
    private boolean isFechaAdopcionValida() {
        Date fechaListoParaAdopcion = animal.getLogsEstado().stream().filter(a -> a.nuevoEstado == EstadoAnimal.LISTO_PARA_ADOPCION).toList().get(0).getFechaCambio();
        return !fechaAdopcion.before(animal.getFechaRescate()) && !fechaAdopcion.before(fechaListoParaAdopcion);
    }

    @AssertTrue(message = "fecha_contacto_no_anterior_a_adopcion")
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
            throw new IllegalArgumentException(XavaResources.getString("vinculacion_incompatibilidad"));
        }
        animal.adoptarAnimal(responsableCentro, fechaAdopcion);
    }
}
