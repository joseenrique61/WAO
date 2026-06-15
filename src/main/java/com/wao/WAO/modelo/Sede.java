package com.wao.WAO.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

import java.util.Collection;

@Entity
@Getter @Setter
public class Sede {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    int id;

    @Column(length=100)
    @Required
    String nombre;

    @Column(length=200)
    @Required
    String direccion;

    int capacidadMaxima;

    @Column(length=100)
    String administradorACargo;

    boolean activa;

    @ElementCollection
    @ListProperties("nombre, especie, estado")
    Collection<Animal> animales;

    public int calcularOcupacionActual() {
        if (animales == null) return 0;
        return (int) animales.stream()
            .filter(a -> a.getEstado() != null &&
                a.getEstado() != com.wao.WAO.modelo.enums.EstadoAnimal.ADOPTADO &&
                a.getEstado() != com.wao.WAO.modelo.enums.EstadoAnimal.FALLECIDO)
            .count();
    }

    public boolean desactivarSede() {
        if (calcularOcupacionActual() > 0) {
            return false;
        }
        activa = false;
        return true;
    }
}
