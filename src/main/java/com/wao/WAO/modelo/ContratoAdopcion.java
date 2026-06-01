package com.wao.WAO.modelo;

import java.util.*;
import java.time.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class ContratoAdopcion {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Required
	Long idContrato;

	@Required
	LocalDate fechaContrato;

	@Column(length=255)
	@Required
	String nombresTestigos;

	@Required
	Boolean firmaDigital;

	@Required
	Boolean terminosAceptados;

	@Column(length=36)
	@File(acceptFileTypes = "application/pdf")
	@Required
	String contratoPDF;

	@OneToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	@Required
	Animal animal;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties = "nombreCompleto")
	@Required
	Adoptante adoptante;

	@OneToMany(mappedBy="contrato", cascade=CascadeType.ALL, orphanRemoval=true)
	@ListProperties("fechaContacto, tipoContacto, notasEstado")
	Collection<SeguimientoPostAdopcion> seguimientos;

	@OneToMany(mappedBy="contrato", cascade=CascadeType.ALL, orphanRemoval=true)
	@ListProperties("fecha, monto, concepto")
	Collection<IngresoFinanciero> ingresos;
}
