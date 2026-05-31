package com.wao.WAO.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class Voluntario {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long idVoluntario;

	@Column(length=20)
	@Required
	String dni;

	@Column(length=150)
	@Required
	String nombreCompleto;

	@Required
	Integer horasAcumuladas;
}
