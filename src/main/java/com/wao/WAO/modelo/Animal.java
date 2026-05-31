package com.wao.WAO.modelo;

import java.util.*;
import java.time.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class Animal {

	@Id
	@Column(length=50)
	String idAnimal;

	@Column(length=100)
	@Required
	String nombre;

	@Column(length=50)
	@Required
	String especie;

	@Column(length=50)
	String raza;

	Integer edadAproximada;

	@Required
	@Enumerated(EnumType.STRING)
	Sexo sexo;

	@Required
	LocalDate fechaRescate;

	@Column(length=255)
	String lugarRescate;

	@Column(length=2000)
	String condicionesHallazgo;

	@Required
	@Enumerated(EnumType.STRING)
	EstadoAnimal estado;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	Sede sede;

	@OneToMany(mappedBy="animal")
	@ListProperties("urlArchivo, esPrincipal")
	Collection<FotografiaMultimedia> fotografias;

	@OneToOne(mappedBy="animal")
	RegistroDefuncion registroDefuncion;

	@OneToMany(mappedBy="animal")
	@ListProperties("fechaConsulta, tipo, diagnostico")
	Collection<BitacoraMedica> historialMedico;

	@OneToMany(mappedBy="animal")
	@ListProperties("tipo, nombreProducto, fechaAplicacion")
	Collection<PlanProfilactico> planProfilactico;

	@OneToMany(mappedBy="animal")
	@ListProperties("medicamento, dosis, fechaInicio, fechaFin")
	Collection<TratamientoMedico> tratamientos;

	@OneToOne(mappedBy="animal")
	ContratoAdopcion contratoAdopcion;

	@OneToMany(mappedBy="animal")
	@ListProperties("montoAporteMensual, fechaInicio, estadoActivo")
	Collection<Apadrinamiento> apadrinamientos;
}
