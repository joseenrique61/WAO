package com.wao.WAO.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class Staff {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long idStaff;

	@Column(length=20)
	@Required
	String dni;

	@Column(length=150)
	@Required
	String nombres;

	@Column(length=50)
	@Required
	String cargo;
}
