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

	@Column(length=36)
	@Required
	@File(acceptFileTypes = "image/png,image/jpg,image/jpeg", maxFileSizeInKb = 5000)
	String imagen;

	@Required
	Boolean esPrincipal;

	// TODO: Calculate this property automatically
	// Double tamanoMB;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	@Required
	Animal animal;
}
