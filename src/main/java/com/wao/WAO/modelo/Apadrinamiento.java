package com.wao.WAO.modelo;

import java.math.*;
import java.time.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class Apadrinamiento {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long idApadrinamiento;

	@Money
	BigDecimal montoAporteMensual;

	@Required
	LocalDate fechaInicio;

	@Required
	Boolean estadoActivo;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	Animal animal;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties = "nombreCompleto")
	Padrino padrino;
}
