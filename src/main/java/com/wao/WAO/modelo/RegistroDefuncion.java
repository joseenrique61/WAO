package com.wao.WAO.modelo;

import java.time.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class RegistroDefuncion {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long idDefuncion;

	@Required
	LocalDate fechaDefuncion;

	@Column(length=255)
	@Required
	String causaFallecimiento;

	@Column(length=2000)
	String notasVeterinario;

	@OneToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	Animal animal;
}
