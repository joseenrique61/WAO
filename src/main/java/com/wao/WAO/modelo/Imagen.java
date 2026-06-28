package com.wao.WAO.modelo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.File;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.Required;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Imagen {
    @Id
    @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(length=32)
    String id;

    @File(maxFileSizeInKb = 5000, acceptFileTypes = "image/png,image/jpg,image/jpeg")
    @Required
    String imagen;

    @ManyToOne(fetch = FetchType.LAZY)
    Animal animal;
}
