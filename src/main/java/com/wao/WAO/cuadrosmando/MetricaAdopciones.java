package com.wao.WAO.cuadrosmando;

import lombok.*;

@Getter @Setter @AllArgsConstructor
public class MetricaAdopciones {

    int totalAdopciones;
    int adopcionesExitosas;
    int seguimientosPendientes;
    double porcentajeExito;

    public double calcularPorcentajeExitoAdopciones() {
        if (totalAdopciones == 0) return 0;
        return adopcionesExitosas * 100.0 / totalAdopciones;
    }
}
