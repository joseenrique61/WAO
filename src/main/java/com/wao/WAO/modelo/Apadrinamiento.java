package com.wao.WAO.modelo;

import java.math.*;
import java.time.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class Apadrinamiento {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Required
	Long idApadrinamiento;

	@Money
	@Required
	@Min(0)
	BigDecimal montoAporteMensual;

	@Required
	LocalDate fechaInicio;

	@Required
	Boolean estadoActivo;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	@Required
	Animal animal;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties = "nombreCompleto")
	@Required
	Padrino padrino;
}
