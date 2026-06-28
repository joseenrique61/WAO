package com.wao.WAO.modelo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.Required;
import org.openxava.annotations.View;

import javax.persistence.*;
import javax.validation.constraints.Past;
import java.util.Date;

@Entity
@Getter
@Setter
@View(name = "Simple", members = "fechaDefuncion; causaFallecimiento")
public class Defuncion {
    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    String id;

    @Required
    @Past
    Date fechaDefuncion;

    @Required
    @Column(length = 200)
    String causaFallecimiento;

    @OneToOne(optional = false)
    @ReferenceView("Simple")
    @Required
    Animal animal;
}
