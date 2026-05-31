package com.wao.WAO.modelo;

import java.util.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class Adoptante {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long idAdoptante;

	@Column(length=20)
	@Required
	String dni;

	@Column(length=150)
	@Required
	String nombreCompleto;

	@Column(length=255)
	@Required
	String direccion;

	@Column(length=20)
	@Required
	String telefono;

	@Column(length=50)
	@Required
	String tipoVivienda;

	@Required
	Boolean tieneNinos;

	@Required
	Boolean tieneOtrasMascotas;

	@Required
	@Enumerated(EnumType.STRING)
	EstadoAdoptante calificacion;

	@OneToMany(mappedBy="adoptante")
	@ListProperties("fechaContrato, animal.nombre")
	Collection<ContratoAdopcion> contratos;

	@OneToMany(mappedBy="adoptante")
	@ListProperties("fecha, rangoHorario, resultadoInteraccion")
	Collection<CitaVisita> citas;

	@OneToMany(mappedBy="adoptante")
	@ListProperties("fecha, hora, modalidad")
	Collection<EntrevistaPreAdopcion> entrevistas;
}
