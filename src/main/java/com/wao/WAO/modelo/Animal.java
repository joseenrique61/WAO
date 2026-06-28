package com.wao.WAO.modelo;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import lombok.*;

import java.util.Collection;
import java.util.Date;
import java.util.ArrayList;

import com.wao.WAO.modelo.enums.*;

@Entity
@Getter
@Setter
@View(name = "Simple", members = "nombre; especie, raza; sexo, edadAproximada")
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

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL)
    @Size(max = 10)
    Collection<Imagen> imagenes;

    @OneToMany(mappedBy = "animal")
    Collection<EntradaClinica> entradasClinicas;

    @OneToMany(mappedBy = "animal")
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
        agregarLog(EstadoAnimal.LISTO_PARA_ADOPCION, usuario);
    }

    public void adoptarAnimal(String adoptante) {
        cambiarEstado(EstadoAnimal.ADOPTADO, adoptante);
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

    public void cambiarEstado(EstadoAnimal nuevoEstado, String usuario) {
        if (!validarTransicionEstado(nuevoEstado)) {
            throw new IllegalArgumentException("Transicion de estado no valida de " + this.estado + " a " + nuevoEstado);
        }
        this.estado = nuevoEstado;
        agregarLog(nuevoEstado, usuario);
    }

    public void registrarDefuncion(Date fecha) {
        this.estado = EstadoAnimal.FALLECIDO;

        this.sede = null;
        agregarLog(EstadoAnimal.FALLECIDO, "Admin");
    }

    private void agregarLog(EstadoAnimal nuevoEstado, String usuario) {
        if (logsEstado == null) {
            logsEstado = new ArrayList<>();
        }
        LogEstadoAnimal log = new LogEstadoAnimal();
        log.setFechaCambio(new Date());
        log.setNuevoEstado(nuevoEstado);
        log.setUsuarioAsigno(usuario);
        logsEstado.add(log);
    }
}
