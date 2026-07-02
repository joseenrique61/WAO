package com.wao.WAO.validadores;

import com.wao.WAO.modelo.Animal;
import com.wao.WAO.modelo.Sede;
import com.wao.WAO.modelo.enums.EstadoAnimal;
import lombok.Getter;
import lombok.Setter;
import org.openxava.jpa.XPersistence;
import org.openxava.util.Messages;
import org.openxava.validators.IValidator;

import java.util.Objects;

@Getter @Setter
public class ValidadorAnimal implements IValidator {
    String id;
    EstadoAnimal estadoNuevo;
    Sede sede;

    @Override
    public void validate(Messages errors) {
        validarEstado(errors);
        validarSede(errors);
    }

    private void validarEstado(Messages errors) {
        if (id == null || id.isEmpty()) {
            if (estadoNuevo != EstadoAnimal.RESCATADO) {
                errors.add("animal_nuevo_estado_invalido");
                return;
            }

            return;
        }

        // Buscamos la entidad tal cual está en la base de datos (valor previo)
        Animal animalEnBaseDeDatos = XPersistence.getManager().find(Animal.class, id);
        if (animalEnBaseDeDatos != null) {
            EstadoAnimal estadoPrevio = animalEnBaseDeDatos.getEstado();

            // 4. APLICAR REGLAS DE NEGOCIO
            if (estadoPrevio != EstadoAnimal.LISTO_PARA_ADOPCION && estadoNuevo == EstadoAnimal.LISTO_PARA_ADOPCION) {
                errors.add("no_se_puede_asignar_listo_para_adopcion_manual");
                return;
            }

            if (estadoPrevio != EstadoAnimal.ADOPTADO && estadoNuevo == EstadoAnimal.ADOPTADO) {
                errors.add("no_se_puede_asignar_adoptado_manual");
                return;
            }

            if (estadoPrevio != EstadoAnimal.FALLECIDO && estadoNuevo == EstadoAnimal.FALLECIDO) {
                errors.add("no_se_puede_asignar_fallecido_manual");
                return;
            }

            if (estadoPrevio == EstadoAnimal.ADOPTADO && estadoNuevo != EstadoAnimal.ADOPTADO) {
                errors.add("no_se_puede_cambiar_estado_animal_adoptado");
                return;
            }

            if (estadoPrevio == EstadoAnimal.FALLECIDO && estadoNuevo != EstadoAnimal.FALLECIDO) {
                // Si intentan revivir al animal, lanzamos error y cortamos la ejecución
                errors.add("no_se_puede_cambiar_el_estado_de_un_animal_fallecido");
            }
        }

    }

    private void validarSede(Messages errors) {
        if (sede == null) {
            errors.add("sede_obligatoria");
            return;
        }

        if (!sede.isActiva() || (sede.calcularOcupacionActual() >= sede.getCapacidadMaxima() && sede.getAnimales().stream().noneMatch(a -> Objects.equals(a.getId(), id)))) {
            errors.add("sede_inactiva_o_llena");
        }

    }
}
