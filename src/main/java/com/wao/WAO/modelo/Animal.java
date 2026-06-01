package com.wao.WAO.modelo;

import java.util.*;
import java.time.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class Animal {

	@Id
	@Column(length=50)
	@Required
	String idAnimal;

	@Column(length=100)
	@Required
	String nombre;

	@Column(length=50)
	@Required
	String especie;

	@Column(length=50)
	String raza;

	@Min(0)
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
	@Required
	Sede sede;

	@OneToMany(mappedBy="animal", cascade=CascadeType.ALL, orphanRemoval=true)
	@ListProperties("esPrincipal")
	Collection<FotografiaMultimedia> fotografias;

	@OneToOne(mappedBy="animal", cascade=CascadeType.ALL)
	RegistroDefuncion registroDefuncion;

	@OneToMany(mappedBy="animal", cascade=CascadeType.ALL, orphanRemoval=true)
	@ListProperties("fechaConsulta, tipo, diagnostico")
	Collection<BitacoraMedica> historialMedico;

	@OneToMany(mappedBy="animal", cascade=CascadeType.ALL, orphanRemoval=true)
	@ListProperties("tipo, nombreProducto, fechaAplicacion")
	Collection<PlanProfilactico> planProfilactico;

	@OneToMany(mappedBy="animal", cascade=CascadeType.ALL, orphanRemoval=true)
	@ListProperties("medicamento, dosis, fechaInicio, fechaFin")
	Collection<TratamientoMedico> tratamientos;

	@OneToOne(mappedBy="animal", cascade=CascadeType.ALL)
	ContratoAdopcion contratoAdopcion;

	@OneToMany(mappedBy="animal", cascade=CascadeType.ALL, orphanRemoval=true)
	@ListProperties("montoAporteMensual, fechaInicio, estadoActivo")
	Collection<Apadrinamiento> apadrinamientos;
}
