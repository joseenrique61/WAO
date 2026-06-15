package com.wao.WAO.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

import java.util.Collection;
import java.util.Date;
import java.util.ArrayList;

import com.wao.WAO.modelo.enums.*;

@Entity
@Getter @Setter
public class ContratoAdopcion {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    int id;

    @ManyToOne(fetch=FetchType.LAZY)
    @DescriptionsList
    Adoptante adoptante;

    @ManyToOne(fetch=FetchType.LAZY)
    @DescriptionsList
    Animal animal;

    @Required
    Date fechaAdopcion;

    @Column(length=100)
    String responsableCentro;

    @ElementCollection
    @ListProperties("fechaContacto, notasEstado, tipoContacto")
    Collection<SeguimientoPostAdopcion> seguimientos;

    public boolean validarCompatibilidad() {
        return adoptante != null && adoptante.getEstadoPerfil() == EstadoPerfil.APTO
            && animal != null && animal.getEstado() == EstadoAnimal.LISTO_PARA_ADOPCION;
    }

    public void procesarVinculacion(Adoptante adoptante, Animal animal) {
        this.adoptante = adoptante;
        this.animal = animal;
        if (!validarCompatibilidad()) {
            throw new IllegalArgumentException("El adoptante debe estar APTO y el animal LISTO_PARA_ADOPCION");
        }
        animal.cambiarEstado(EstadoAnimal.ADOPTADO, adoptante.getNombre());
    }

    public boolean validarAnimalEstaAdoptado() {
        return animal != null && animal.getEstado() == EstadoAnimal.ADOPTADO;
    }

    public void registrarSeguimiento(Date fechaContacto, String notasEstado, TipoContacto tipoContacto, Date proximaFecha) {
        if (!validarAnimalEstaAdoptado()) {
            throw new IllegalArgumentException("Solo se pueden registrar seguimientos para animales adoptados");
        }
        if (seguimientos == null) {
            seguimientos = new ArrayList<>();
        }
        SeguimientoPostAdopcion seguimiento = new SeguimientoPostAdopcion();
        seguimiento.setFechaContacto(fechaContacto);
        seguimiento.setNotasEstado(notasEstado);
        seguimiento.setTipoContacto(tipoContacto);
        seguimiento.setProximaFechaSeguimiento(proximaFecha);
        seguimientos.add(seguimiento);
    }
}
