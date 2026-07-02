package com.wao.WAO.cuadrosmando;

import lombok.*;

@Getter @Setter @AllArgsConstructor
public class MetricaPoblacion {

    int totalAnimales;
    int rescatados;
    int enObservacion;
    int listosAdopcion;
    int adoptados;
    int fallecidos;
    int ocupacionActual;
    int capacidadTotal;

    public double calcularPorcentajeOcupacion() {
        if (capacidadTotal == 0) return 0;
        return (double) (ocupacionActual * 100) / capacidadTotal;
    }

    public double calcularPorcentajeFallecido() {
        if (totalAnimales == 0) return 0;
        return (double) (fallecidos * 100) / totalAnimales;
    }
}
