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

	@Transient
	@Depends("estado") // Se recalcula si cambia el estado
	@Stereotype("LABEL") // Lo muestra como un texto resaltado en la UI
	public String getRequisitosAdopcion() {
		// Llamamos al algoritmo que construimos
		boolean apto = evaluarDisponibilidadAdopcion();

		if (apto) {
			return "CUMPLE REQUISITOS CLÍNICOS Y PROFILÁCTICOS";
		} else {
			return "FALTAN REQUISITOS (Revisar Vacunas o Patologías)";
		}
	}

	/**
	 * Algoritmo de validación estructural (RF-02)
	 * Verifica si el animal cumple los requisitos para ser puesto en adopción.
	 */
	public boolean evaluarDisponibilidadAdopcion() {

		// Regla 1: Validar que no esté ya en estado terminal o adoptado
		if (this.estado == EstadoAnimal.ADOPTADO || this.estado == EstadoAnimal.FALLECIDO) {
			return false;
		}

		// Regla 2: Recorrer bitácora para descartar patologías contagiosas activas
		if (this.historialMedico != null && !this.historialMedico.isEmpty()) {
			for (BitacoraMedica registro : this.historialMedico) {
				if (registro.getEsContagiosa()) {
					return false; // Falla validación clínica
				}
			}
		}

		// Regla 3: Validar esquema de vacunación y desparasitación
		boolean tieneVacuna = false;
		boolean tieneDesparasitante = false;

		if (this.planProfilactico != null && !this.planProfilactico.isEmpty()) {
			for (PlanProfilactico profilaxis : this.planProfilactico) {
				if (profilaxis.getTipo() == TipoProfilaxis.VACUNA) {
					tieneVacuna = true;
				} else if (profilaxis.getTipo() == TipoProfilaxis.DESPARASITANTE) {
					tieneDesparasitante = true;
				}
			}
		}

		// Si alguna bandera quedó en false, falla la validación
		if (!tieneVacuna || !tieneDesparasitante) {
			return false; // Falla validación profiláctica
		}

		// Ruta de éxito: Cumple todos los requisitos
		return true;
	}
}
