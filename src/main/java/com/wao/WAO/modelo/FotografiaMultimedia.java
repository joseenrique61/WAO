package com.wao.WAO.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class FotografiaMultimedia {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long idFoto;

	@Column(length=255)
	@Required
	String urlArchivo;

	@Required
	Boolean esPrincipal;

	Double tamanoMB;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	Animal animal;
}
