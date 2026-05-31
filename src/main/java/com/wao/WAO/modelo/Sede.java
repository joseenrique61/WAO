package com.wao.WAO.modelo;

import java.util.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class Sede {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long idSede;

	@Column(length=100)
	@Required
	String nombre;

	@Column(length=255)
	@Required
	String direccion;

	@Required
	Integer capacidadMaxima;

	@Column(length=100)
	String administradorACargo;

	@Required
	Boolean estaActiva;

	@OneToMany(mappedBy="sede")
	@ListProperties("nombre, especie, estado")
	Collection<Animal> animales;

	@OneToMany(mappedBy="sede")
	@ListProperties("nombreInsumo, lote, stockActual")
	Collection<InsumoMedico> insumos;

	@OneToMany(mappedBy="sede")
	@ListProperties("fecha, rangoHorario, resultadoInteraccion")
	Collection<CitaVisita> citas;

	@OneToMany(mappedBy="sede")
	@ListProperties("fecha, rangoHorario, areaAsignada")
	Collection<TurnoVoluntariado> turnos;
}
