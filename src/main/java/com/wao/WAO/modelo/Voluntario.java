package com.wao.WAO.modelo;

import java.util.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class Voluntario {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Required
	Long idVoluntario;

	@Column(length=20, unique=true)
	@Required
	String dni;

	@Column(length=150)
	@Required
	String nombreCompleto;

	@Required
	Integer horasAcumuladas;

	@OneToMany(mappedBy="voluntario")
	@ListProperties("fecha, rangoHorario, areaAsignada, sede.nombre")
	Collection<TurnoVoluntariado> turnos;
}
