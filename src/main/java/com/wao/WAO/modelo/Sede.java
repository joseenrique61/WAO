package com.wao.WAO.modelo;

import javax.persistence.*;

import com.wao.WAO.modelo.enums.EstadoAnimal;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import lombok.*;

import java.util.Collection;

@Entity
@Getter
@Setter
public class Sede {

    @Id
    @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(length=32)
    String id;

    @Column(length = 100)
    @Required
    String nombre;

    @Column(length = 200)
    @Required
    String direccion;

    int capacidadMaxima;

    @Column(length = 100)
    String administradorACargo;

    boolean activa;

//    @ElementCollection
    @ListProperties("nombre, especie, estado")
    @OneToMany(mappedBy = "sede")
    Collection<Animal> animales;

    public int calcularOcupacionActual() {
        if (animales == null) return 0;
        return (int) animales.stream()
                .filter(a -> a.getEstado() != null &&
                        a.getEstado() != EstadoAnimal.ADOPTADO &&
                        a.getEstado() != EstadoAnimal.FALLECIDO)
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
