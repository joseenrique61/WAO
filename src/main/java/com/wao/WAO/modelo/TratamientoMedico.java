package com.wao.WAO.modelo;

import java.util.*;
import java.time.*;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class TratamientoMedico {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Required
	Long idTratamiento;

	@Column(length=100)
	@Required
	String medicamento;

	@Column(length=50)
	@Required
	String dosis;

	@Required
	@Min(1)
	Integer frecuenciaHoras;

	@Required
	LocalDate fechaInicio;

	@Required
	LocalDate fechaFin;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	@Required
	Animal animal;

	@OneToMany(mappedBy="tratamiento", cascade=CascadeType.ALL, orphanRemoval=true)
	@ListProperties("fechaHoraEsperada, confirmacionDada, estadoToma")
	Collection<AlertaDiariaMedicacion> alertas;

	@AssertTrue(message="La fecha de fin debe ser posterior a la fecha de inicio")
	public boolean isFechaFinAfterFechaInicio() {
		return fechaFin == null || fechaInicio == null || !fechaFin.isBefore(fechaInicio);
	}
}
