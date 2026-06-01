package com.wao.WAO.modelo;

import java.time.*;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class PlanProfilactico {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Required
	Long idProfilaxis;

	@Required
	@Enumerated(EnumType.STRING)
	TipoProfilaxis tipo;

	@Column(length=100)
	@Required
	String nombreProducto;

	@Required
	LocalDate fechaAplicacion;

	LocalDate fechaProximoRefuerzo;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	@Required
	Animal animal;

	@AssertTrue(message="La fecha de próximo refuerzo debe ser posterior a la fecha de aplicación")
	public boolean isFechaRefuerzoAfterAplicacion() {
		return fechaProximoRefuerzo == null || fechaAplicacion == null || !fechaProximoRefuerzo.isBefore(fechaAplicacion);
	}
}
