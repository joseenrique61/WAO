package com.wao.WAO.modelo;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import com.wao.WAO.validadores.ValidadorAnimal;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.util.XavaResources;
import lombok.*;

import java.util.Collection;
import java.util.Date;
import java.util.ArrayList;

import com.wao.WAO.modelo.enums.*;

@Entity
@Getter
@Setter
@View(name = "Simple", members = "nombre; especie, raza; sexo, edadAproximada")
@EntityValidator(value = ValidadorAnimal.class,
        properties = {
                @PropertyValue(name = "id"),
                @PropertyValue(name = "estadoNuevo", from = "estado"),
                @PropertyValue(name = "sede"),
        }
)
public class Animal {
    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @DescriptionsList
    Sede sede;

    @Column(length = 100)
    @Required
    String nombre;

    @Column(length = 50)
    @Required
    String especie;

    @Column(length = 50)
    String raza;

    @Min(0)
    int edadAproximada;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Required
    SexoAnimal sexo;

    @Required
    @Past
    Date fechaRescate;

    @Column(length = 200)
    String lugarRescate;

    @Column(length = 1000)
    String condicionesHallazgo;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    @Required
    EstadoAnimal estado;

    @Column(length = 1000)
    @TextArea
    String notasVeterinario;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Size(max = 10)
    Collection<Imagen> imagenes;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    @XOrderBy("fechaConsulta")
    Collection<EntradaClinica> entradasClinicas;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    @XOrderBy("fechaAplicacion")
    Collection<TratamientoProfilactico> tratamientosProfilacticos;

    @OneToOne(mappedBy = "animal")
    @ReadOnly
    @ReferenceView("Simple")
    Defuncion defuncion;

    @ReadOnly
    @ElementCollection
    @ListProperties("fechaCambio, nuevoEstado, usuarioAsigno")
    Collection<LogEstadoAnimal> logsEstado;

    public boolean validarCondicionesParaAdopcion() {

        // Regla 1: Validar que no está ya en estado terminal o adoptado
        if (this.estado == EstadoAnimal.ADOPTADO || this.estado == EstadoAnimal.FALLECIDO) {
            return false;
        }

        // Regla 2: Recorrer bitácora para descartar patologías contagiosas activas
        if (this.entradasClinicas != null && !this.entradasClinicas.isEmpty()) {
            for (EntradaClinica registro : this.entradasClinicas) {
                if (registro.isEsContagiosa()) {
                    return false; // Retorno anticipado: Falla validación clínica
                }
            }
        }

        // Regla 3: Validar esquema de vacunación y desparasitación
        boolean tieneVacuna = false;
        boolean tieneDesparasitante = false;

        if (this.tratamientosProfilacticos != null && !this.tratamientosProfilacticos.isEmpty()) {
            for (TratamientoProfilactico profilaxis : this.tratamientosProfilacticos) {
                if (profilaxis.getTipo() == TipoProfilactico.VACUNA) {
                    tieneVacuna = true;
                } else if (profilaxis.getTipo() == TipoProfilactico.DESPARASITANTE) {
                    tieneDesparasitante = true;
                }
            }
        }

        // Si alguna bandera quedó en false, falla la validación
        return tieneVacuna && tieneDesparasitante;
    }

    public void establecerListoParaAdopcion(String usuario) {
        if (!validarCondicionesParaAdopcion()) {
            return;
        }

        this.estado = EstadoAnimal.LISTO_PARA_ADOPCION;
        agregarLog(EstadoAnimal.LISTO_PARA_ADOPCION, usuario, null);
    }

    public void adoptarAnimal(String usuario, Date fecha) {
        cambiarEstado(EstadoAnimal.ADOPTADO, usuario, fecha);
    }

    public boolean validarTransicionEstado(EstadoAnimal nuevoEstado) {
        if (this.estado == null) return true;
        return switch (this.estado) {
            case RESCATADO -> nuevoEstado == EstadoAnimal.EN_OBSERVACION || nuevoEstado == EstadoAnimal.FALLECIDO;
            case EN_OBSERVACION ->
                    nuevoEstado == EstadoAnimal.LISTO_PARA_ADOPCION || nuevoEstado == EstadoAnimal.RESCATADO || nuevoEstado == EstadoAnimal.FALLECIDO;
            case LISTO_PARA_ADOPCION ->
                    nuevoEstado == EstadoAnimal.ADOPTADO || nuevoEstado == EstadoAnimal.EN_OBSERVACION || nuevoEstado == EstadoAnimal.FALLECIDO;
            default -> false;
        };
    }

    public void cambiarEstado(EstadoAnimal nuevoEstado, String usuario, Date fecha) {
        if (!validarTransicionEstado(nuevoEstado)) {
            throw new IllegalArgumentException(XavaResources.getString("transicion_estado_no_valida", this.estado, nuevoEstado));
        }
        this.estado = nuevoEstado;
        agregarLog(nuevoEstado, usuario, fecha);
    }

    public void registrarDefuncion(String usuario, Date fecha) {
        this.estado = EstadoAnimal.FALLECIDO;

        this.sede = null;
        agregarLog(EstadoAnimal.FALLECIDO, usuario, fecha);
    }

    private void agregarLog(EstadoAnimal nuevoEstado, String usuario, Date fecha) {
        if (logsEstado == null) {
            logsEstado = new ArrayList<>();
        }
        LogEstadoAnimal log = new LogEstadoAnimal();
        log.setFechaCambio(fecha == null ? new Date() : fecha);
        log.setNuevoEstado(nuevoEstado);
        log.setUsuarioAsigno(usuario);
        logsEstado.add(log);
    }
}
