package com.wao.WAO.modelo;

import java.time.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class PlanProfilactico {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
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
	Animal animal;
}
