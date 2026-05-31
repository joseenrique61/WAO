package com.wao.WAO.modelo;

import java.util.*;
import java.time.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class TratamientoMedico {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long idTratamiento;

	@Column(length=100)
	@Required
	String medicamento;

	@Column(length=50)
	@Required
	String dosis;

	@Required
	Integer frecuenciaHoras;

	@Required
	LocalDate fechaInicio;

	@Required
	LocalDate fechaFin;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	Animal animal;

	@OneToMany(mappedBy="tratamiento")
	@ListProperties("fechaHoraEsperada, confirmacionDada, estadoToma")
	Collection<AlertaDiariaMedicacion> alertas;
}
