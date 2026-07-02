package com.wao.WAO.modelo;

import javax.persistence.*;
import javax.validation.ValidationException;

import com.wao.WAO.modelo.enums.EstadoAnimal;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.util.XavaResources;
import lombok.*;

import java.util.Collection;

@Entity
@Getter
@Setter
public class Sede {
    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
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

    @PreRemove
    private void validarAntesDeBorrar() {
        if (animales != null && !animales.isEmpty()) {
            // Al lanzar esta excepción, OpenXava detiene el borrado
            // y muestra el mensaje en la pantalla automáticamente.
            throw new ValidationException(XavaResources.getString("sede_con_animales_no_eliminable"));
        }
    }
}
