package com.wao.WAO.acciones;

import com.wao.WAO.modelo.Animal;
import com.wao.WAO.modelo.Sede;
import com.wao.WAO.modelo.enums.EstadoAnimal;
import org.openxava.actions.SaveAction;
import org.openxava.jpa.XPersistence;

public class BloquearGuardadoAnimal extends SaveAction {
    @Override
    public void execute() throws Exception {
        // 1. Obtener el ID de la vista (para buscar el registro actual en la DB)
        Animal animal = (Animal) getView().getEntity();

        if (!validarEstado(animal) || !validarSede(animal)) {
            return;
        }

        // 5. Si todo está bien, ejecutar el guardado normal
        super.execute();
    }

    private boolean validarEstado(Animal animal) {
        EstadoAnimal estadoNuevo = animal.getEstado();
        if (animal.getId() == null || animal.getId().isEmpty()) {
            if (estadoNuevo != EstadoAnimal.RESCATADO) {
                addError("Un animal nuevo solo puede tener estado RESCATADO.");
                return false;
            }

            return true;
        }

        // Buscamos la entidad tal cual está en la base de datos (valor previo)
        Animal animalEnBaseDeDatos = XPersistence.getManager().find(Animal.class, animal.getId());
        if (animalEnBaseDeDatos != null) {
            EstadoAnimal estadoPrevio = animalEnBaseDeDatos.getEstado();

            // 4. APLICAR REGLAS DE NEGOCIO
            if (estadoPrevio != EstadoAnimal.LISTO_PARA_ADOPCION && estadoNuevo == EstadoAnimal.LISTO_PARA_ADOPCION) {
                addError("No se puede asignar el estado listo para adopción manualmente.");
                return false;
            }

            if (estadoPrevio != EstadoAnimal.ADOPTADO && estadoNuevo == EstadoAnimal.ADOPTADO) {
                addError("No se puede asignar el estado adoptado manualmente.");
                return false;
            }

            if (estadoPrevio != EstadoAnimal.FALLECIDO && estadoNuevo == EstadoAnimal.FALLECIDO) {
                addError("No se puede asignar el estado fallecido manualmente.");
                return false;
            }

            if (estadoPrevio == EstadoAnimal.ADOPTADO && estadoNuevo != EstadoAnimal.ADOPTADO) {
                addError("No se puede cambiar el estado de un animal adoptado.");
                return false;
            }

            if (estadoPrevio == EstadoAnimal.FALLECIDO && estadoNuevo != EstadoAnimal.FALLECIDO) {
                // Si intentan revivir al animal, lanzamos error y cortamos la ejecución
                addError("No se puede cambiar el estado de un animal fallecido.");
                return false;
            }
        }

        return true;
    }

    private boolean validarSede(Animal animal) {
        Sede sede = animal.getSede();

        if (sede == null) {
            addError("La sede es obligatoria.");
            return false;
        }

        if (!sede.isActiva() || sede.calcularOcupacionActual() >= sede.getCapacidadMaxima()) {
            addError("La sede no está activa o está al límite.");
            return false;
        }

        return true;
    }
}
