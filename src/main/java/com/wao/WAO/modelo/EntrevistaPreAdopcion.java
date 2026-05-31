package com.wao.WAO.modelo;

import java.time.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class EntrevistaPreAdopcion {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long idEntrevista;

	@Required
	LocalDate fecha;

	@Required
	LocalTime hora;

	@Required
	@Enumerated(EnumType.STRING)
	ModalidadEntrevista modalidad;

	@Column(length=255)
	String enlaceReunion;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties = "nombreCompleto")
	Adoptante adoptante;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties = "nombres")
	Staff evaluador;
}
