package com.wao.WAO.validadores;

import org.openxava.util.Messages;
import org.openxava.validators.IPropertyValidator;

public class ValidadorCedula implements IPropertyValidator {

    private static final int[] COEFICIENTES = { 2, 1, 2, 1, 2, 1, 2, 1, 2 };

    @Override
    public void validate(Messages errors, Object value, String propertyName, String modelName) {
        if (value == null) {
            errors.add("cedula_obligatoria", propertyName);
            return;
        }

        String cedula = value.toString().trim();

        if (!cedula.matches("\\d{10}")) {
            errors.add("cedula_formato_invalido", propertyName);
            return;
        }

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if ((provincia < 1 || provincia > 24) && provincia != 30) {
            errors.add("cedula_provincia_invalida", propertyName);
            return;
        }

        int tercerDigito = Character.getNumericValue(cedula.charAt(2));
        if (tercerDigito < 0 || tercerDigito > 5) {
            errors.add("cedula_tercer_digito_invalido", propertyName);
            return;
        }

        int digitoVerificadorCalculado = calcularDigitoVerificador(cedula.substring(0, 9));
        int digitoVerificadorIngresado = Character.getNumericValue(cedula.charAt(9));

        if (digitoVerificadorCalculado != digitoVerificadorIngresado) {
            errors.add("cedula_invalida", propertyName);
        }
    }

    private int calcularDigitoVerificador(String nueveDigitos) {
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int digito = Character.getNumericValue(nueveDigitos.charAt(i));
            int producto = digito * COEFICIENTES[i];
            suma += (producto >= 10) ? (producto - 9) : producto;
        }

        int modulo = suma % 10;
        return (modulo == 0) ? 0 : 10 - modulo;
    }
}