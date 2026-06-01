package com.wao.WAO.modelo;

import java.util.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class Staff {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Required
	Long idStaff;

	@Column(length=20, unique=true)
	@Required
	String dni;

	@Column(length=150)
	@Required
	String nombres;

	@Column(length=50)
	@Required
	String cargo;

	@OneToMany(mappedBy="tratante")
	@ListProperties("animal.nombre, fechaConsulta, tipo, diagnostico")
	Collection<BitacoraMedica> registrosMedicos;

	@OneToMany(mappedBy="evaluador")
	@ListProperties("fecha, adoptante.nombreCompleto, modalidad")
	Collection<EntrevistaPreAdopcion> entrevistas;

	@OneToMany(mappedBy="staffConfirmo")
	@ListProperties("fechaHoraEsperada, tratamiento.medicamento, confirmacionDada, estadoToma")
	Collection<AlertaDiariaMedicacion> confirmaciones;
}
