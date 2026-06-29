package com.wao.WAO.validadores;

import com.wao.WAO.modelo.Entrevista;
import com.wao.WAO.modelo.enums.EstadoPerfil;
import lombok.Getter;
import lombok.Setter;
import org.openxava.util.Messages;
import org.openxava.validators.IValidator;

import java.util.Collection;

@Getter
@Setter
public class ValidadorAdoptante implements IValidator {
    EstadoPerfil estadoPerfil;
    Collection<Entrevista> entrevistas;

    @Override
    public void validate(Messages errors) {
        if (estadoPerfil == EstadoPerfil.APTO && (entrevistas == null || entrevistas.isEmpty())) {
            errors.add("adoptante_sin_entrevistas");
        }
    }
}
