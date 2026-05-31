package com.wao.WAO.modelo;

import java.time.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class AlertaDiariaMedicacion {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long idAlerta;

	@Required
	LocalDateTime fechaHoraEsperada;

	@Required
	Boolean confirmacionDada;

	@Column(length=50)
	@Required
	String estadoToma;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties = "medicamento")
	TratamientoMedico tratamiento;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties = "nombres")
	Staff staffConfirmo;
}
