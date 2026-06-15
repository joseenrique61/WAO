package com.wao.WAO.acciones;

import com.wao.WAO.modelo.Animal;
import com.wao.WAO.modelo.Entrevista;
import com.wao.WAO.modelo.enums.EstadoAnimal;
import com.wao.WAO.modelo.enums.EstadoPerfil;
import org.openxava.actions.SaveAction;
import org.openxava.jpa.XPersistence;

import java.util.Collection;

public class BloquearGuardadoAnimal extends SaveAction {
    @Override
    public void execute() throws Exception {
        // 1. Obtener el ID de la vista (para buscar el registro actual en la DB)
        Object id = getView().getValue("id"); // Asegúrate de que tu propiedad clave se llame "id"

        // 2. Obtener el valor ACTUAL de la pantalla (lo que el usuario eligió)
        EstadoAnimal estadoNuevo = (EstadoAnimal) getView().getValue("estado");

        // 3. Si el ID no es nulo, significa que estamos editando (no creando uno nuevo)
        if (id != null && !"".equals(id.toString())) {

            // Buscamos la entidad tal cual está en la base de datos (valor previo)
            Animal animalEnBaseDeDatos = XPersistence.getManager().find(Animal.class, id);

            if (animalEnBaseDeDatos != null) {
                EstadoAnimal estadoPrevio = animalEnBaseDeDatos.getEstado();

                // 4. APLICAR REGLAS DE NEGOCIO
                if (estadoPrevio != EstadoAnimal.LISTO_PARA_ADOPCION && estadoNuevo == EstadoAnimal.LISTO_PARA_ADOPCION) {
                    addError("No se puede asignar el estado listo para adopción manualmente.");
                    return;
                }

                if (estadoPrevio != EstadoAnimal.ADOPTADO && estadoNuevo == EstadoAnimal.ADOPTADO) {
                    addError("No se puede asignar el estado adoptado manualmente.");
                    return;
                }

                if (estadoPrevio != EstadoAnimal.FALLECIDO && estadoNuevo == EstadoAnimal.FALLECIDO) {
                    addError("No se puede asignar el estado fallecido manualmente.");
                    return;
                }

                if (estadoPrevio == EstadoAnimal.ADOPTADO && estadoNuevo != EstadoAnimal.ADOPTADO) {
                    addError("No se puede cambiar el estado de un animal adoptado.");
                    return;
                }

                if (estadoPrevio == EstadoAnimal.FALLECIDO && estadoNuevo != EstadoAnimal.FALLECIDO) {
                    // Si intentan revivir al animal, lanzamos error y cortamos la ejecución
                    addError("No se puede cambiar el estado de un animal fallecido.");
                    return; // Al no llamar a super.execute(), no se guarda nada
                }
            }
        }

        // 5. Si todo está bien, ejecutar el guardado normal
        super.execute();
    }
}
