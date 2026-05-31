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
	Long idContrato;

	@Required
	LocalDate fechaContrato;

	@Column(length=255)
	String nombresTestigos;

	@Required
	Boolean firmaDigital;

	@Required
	Boolean terminosAceptados;

	@Column(length=255)
	String urlPDFContrato;

	@OneToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	Animal animal;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties = "nombreCompleto")
	Adoptante adoptante;

	@OneToMany(mappedBy="contrato")
	@ListProperties("fechaContacto, tipoContacto, notasEstado")
	Collection<SeguimientoPostAdopcion> seguimientos;

	@OneToMany(mappedBy="contrato")
	@ListProperties("fecha, monto, concepto")
	Collection<IngresoFinanciero> ingresos;
}
