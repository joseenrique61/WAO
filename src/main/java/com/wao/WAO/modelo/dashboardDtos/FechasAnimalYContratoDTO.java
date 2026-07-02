package com.wao.WAO.modelo.dashboardDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class FechasAnimalYContratoDTO {
    Date fechaAdopcion;
    Date fechaRescate;
}
