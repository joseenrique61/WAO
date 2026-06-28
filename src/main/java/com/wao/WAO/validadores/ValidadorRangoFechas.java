package com.wao.WAO.validadores;

import lombok.Getter;
import lombok.Setter;
import org.openxava.util.Messages;
import org.openxava.validators.*;

import java.util.Date;

@Setter
@Getter
public class ValidadorRangoFechas implements IValidator {
    // Getters y Setters para que OpenXava inyecte los valores
    private Date fechaA;
    private Date fechaLimite;
    private String mensajeError;

    public void validate(Messages errors) {
        if (fechaA != null && fechaLimite != null) {
            if (fechaA.before(fechaLimite)) {
                errors.add(mensajeError);
            }
        }
    }
}
