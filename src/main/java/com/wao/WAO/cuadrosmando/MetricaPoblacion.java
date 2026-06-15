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

    public int calcularPorcentajeOcupacion() {
        if (capacidadTotal == 0) return 0;
        return (ocupacionActual * 100) / capacidadTotal;
    }
}
