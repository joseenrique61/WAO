package com.wao.WAO.modelo;

import java.util.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class Padrino {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long idPadrino;

	@Column(length=20)
	@Required
	String dni;

	@Column(length=150)
	@Required
	String nombreCompleto;

	@Column(length=100)
	@Required
	String contacto;

	@OneToMany(mappedBy="padrino")
	@ListProperties("animal.nombre, montoAporteMensual, fechaInicio, estadoActivo")
	Collection<Apadrinamiento> apadrinamientos;
}
