package com.wao.WAO.modelo;

import java.time.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class InsumoMedico {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Required
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
	@Min(0)
	Integer stockActual;

	@Required
	@Min(0)
	Integer stockMinimo;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	@Required
	Sede sede;
}
