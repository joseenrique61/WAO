package com.wao.WAO.modelo;

import java.math.*;
import java.time.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class IngresoFinanciero {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long idIngreso;

	@Required
	LocalDate fecha;

	@Money
	BigDecimal monto;

	@Required
	@Enumerated(EnumType.STRING)
	ConceptoIngreso concepto;

	@Column(length=50)
	@Required
	String metodoPago;

	Long idPagadorAsociado;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties = "idContrato")
	ContratoAdopcion contrato;
}
