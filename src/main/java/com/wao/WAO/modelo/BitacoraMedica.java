package com.wao.WAO.modelo;

import java.time.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class BitacoraMedica {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long idRegistro;

	@Required
	LocalDate fechaConsulta;

	@Required
	@Enumerated(EnumType.STRING)
	TipoPatologia tipo;

	@Column(length=2000)
	@Required
	String diagnostico;

	@Required
	Boolean esContagiosa;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	Animal animal;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties = "nombres")
	Staff tratante;
}
