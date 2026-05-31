package com.wao.WAO.modelo;

import java.time.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class SeguimientoPostAdopcion {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long idSeguimiento;

	@Required
	LocalDate fechaContacto;

	@Required
	@Enumerated(EnumType.STRING)
	TipoSeguimiento tipoContacto;

	@Column(length=2000)
	String notasEstado;

	LocalDate fechaProximoContacto;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties = "idContrato")
	ContratoAdopcion contrato;
}
