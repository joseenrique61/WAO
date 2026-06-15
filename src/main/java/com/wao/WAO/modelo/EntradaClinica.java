package com.wao.WAO.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

import java.util.Date;

import com.wao.WAO.modelo.enums.*;

@Entity
@Getter @Setter
public class EntradaClinica {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    int id;

    @ManyToOne(fetch=FetchType.LAZY)
    @DescriptionsList
    Animal animal;

    @Required
    Date fechaConsulta;

    @Enumerated(EnumType.STRING)
    @Column(length=50)
    @Required
    TipoEntradaClinica tipo;

    @Column(length=1000)
    @Required
    String diagnostico;

    @Column(length=100)
    String veterinarioTratante;

    boolean esContagiosa;

    public void registrarEntrada() {
        if (esContagiosa) {
            throw new IllegalStateException("alerta_patologia_contagiosa");
        }
    }
}
