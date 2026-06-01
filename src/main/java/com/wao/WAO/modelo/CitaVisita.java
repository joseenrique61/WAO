package com.wao.WAO.modelo;

import java.time.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class CitaVisita {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Required
	Long idCita;

	@Required
	LocalDate fecha;

	@Required
	@Enumerated(EnumType.STRING)
	RangoHorario rangoHorario;

	@Required
	@Enumerated(EnumType.STRING)
	ResultadoVisita resultadoInteraccion;

	@Column(length=2000)
	String notas;

	@Required
	Boolean esWalkIn;

	LocalTime horaLlegadaReal;

	@Column(length=20)
	@Required
	String gafeteAsignado;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	@Required
	Sede sede;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties = "nombreCompleto")
	@Required
	Adoptante adoptante;
}
