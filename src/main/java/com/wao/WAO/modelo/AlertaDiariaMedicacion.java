package com.wao.WAO.modelo;

import java.time.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class AlertaDiariaMedicacion {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Required
	Long idAlerta;

	@Required
	LocalDateTime fechaHoraEsperada;

	@Required
	@Enumerated(EnumType.STRING)
	EstadoToma estadoToma;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties = "medicamento")
	TratamientoMedico tratamiento;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties = "nombres")
	Staff staffConfirmo;

	public boolean isConfirmacionDada() {
		return estadoToma == EstadoToma.TOMADO;
	}
}
