package com.wao.WAO.modelo;

import java.time.*;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class SeguimientoPostAdopcion {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Required
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
	@Required
	ContratoAdopcion contrato;

	@AssertTrue(message="La fecha de próximo contacto debe ser posterior a la fecha de contacto")
	public boolean isFechaProximoContactoAfterFechaContacto() {
		return fechaProximoContacto == null || fechaContacto == null || !fechaProximoContacto.isBefore(fechaContacto);
	}
}
