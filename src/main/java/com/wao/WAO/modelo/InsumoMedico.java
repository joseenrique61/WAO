package com.wao.WAO.modelo;

import java.time.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class InsumoMedico {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long idInsumo;

	@Column(length=100)
	@Required
	String nombreInsumo;

	@Column(length=50)
	@Required
	String lote;

	@Required
	LocalDate fechaCaducidad;

	@Required
	Integer stockActual;

	@Required
	Integer stockMinimo;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	Sede sede;
}
