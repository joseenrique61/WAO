package com.wao.WAO.modelo;

import java.time.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class TurnoVoluntariado {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Required
	Long idTurno;

	@Required
	LocalDate fecha;

	@Required
	@Enumerated(EnumType.STRING)
	RangoHorario rangoHorario;

	@Column(length=100)
	@Required
	String areaAsignada;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	@Required
	Sede sede;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties = "nombreCompleto")
	@Required
	Voluntario voluntario;
}
