package com.wao.WAO.modelo;

import java.math.*;
import java.time.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class IngresoFinanciero {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Required
	Long idIngreso;

	@Required
	LocalDate fecha;

	@Money
	@Required
	@Min(0)
	BigDecimal monto;

	@Required
	@Enumerated(EnumType.STRING)
	ConceptoIngreso concepto;

	@Column(length=50)
	@Required
	String metodoPago;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties = "idContrato")
	ContratoAdopcion contrato;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties = "nombreCompleto")
	Padrino padrino;
}
