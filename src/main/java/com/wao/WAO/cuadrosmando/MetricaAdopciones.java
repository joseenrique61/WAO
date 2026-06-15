package com.wao.WAO.cuadrosmando;

import java.math.*;
import lombok.*;

@Getter @Setter @AllArgsConstructor
public class MetricaAdopciones {

    int totalAdopciones;
    int adopcionesExitosas;
    int seguimientosPendientes;
    BigDecimal porcentajeExito;

    public BigDecimal calcularPorcentajeExitoAdopciones() {
        if (totalAdopciones == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(adopcionesExitosas * 100.0 / totalAdopciones);
    }
}
