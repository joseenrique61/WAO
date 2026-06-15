package com.wao.WAO.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

import java.util.Collection;
import java.util.Date;
import java.util.ArrayList;

import com.wao.WAO.modelo.enums.*;

@Entity
@Getter @Setter
public class Animal {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    int id;

    @ManyToOne(fetch=FetchType.LAZY)
    @DescriptionsList
    Sede sede;

    @Column(length=100)
    @Required
    String nombre;

    @Column(length=50)
    @Required
    String especie;

    @Column(length=50)
    String raza;

    int edadAproximada;

    @Column(length=10)
    @Required
    String sexo;

    @Required
    Date fechaRescate;

    @Column(length=200)
    String lugarRescate;

    @Column(length=1000)
    String condicionesHallazgo;

    @Enumerated(EnumType.STRING)
    @Column(length=30)
    @Required
    EstadoAnimal estado;

    Date fechaDefuncion;

    @Column(length=200)
    String causaFallecimiento;

    @Column(length=1000)
    String notasVeterinario;

    @OneToMany(mappedBy = "animal")
    Collection<EntradaClinica> entradasClinicas;

    @OneToMany(mappedBy = "animal")
    Collection<TratamientoProfilactico> tratamientosProfilacticos;

    @ReadOnly
    @ElementCollection
    @ListProperties("fechaCambio, nuevoEstado, usuarioAsigno")
    Collection<LogEstadoAnimal> logsEstado;

    public boolean validarCondicionesParaAdopcion() {

        // Regla 1: Validar que no est� ya en estado terminal o adoptado
        if (this.estado == EstadoAnimal.ADOPTADO || this.estado == EstadoAnimal.FALLECIDO) {
            return false;
        }

        // Regla 2: Recorrer bit�cora para descartar patolog�as contagiosas activas
        if (this.entradasClinicas != null && !this.entradasClinicas.isEmpty()) {
            for (EntradaClinica registro : this.entradasClinicas) {
                if (registro.isEsContagiosa()) {
                    return false; // Retorno anticipado: Falla validaci�n cl�nica
                }
            }
        }

        // Regla 3: Validar esquema de vacunaci�n y desparasitaci�n
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
        switch (this.estado) {
            case RESCATADO:
                return nuevoEstado == EstadoAnimal.EN_OBSERVACION || nuevoEstado == EstadoAnimal.FALLECIDO;
            case EN_OBSERVACION:
                return nuevoEstado == EstadoAnimal.LISTO_PARA_ADOPCION || nuevoEstado == EstadoAnimal.RESCATADO || nuevoEstado == EstadoAnimal.FALLECIDO;
            case LISTO_PARA_ADOPCION:
                return nuevoEstado == EstadoAnimal.ADOPTADO || nuevoEstado == EstadoAnimal.EN_OBSERVACION || nuevoEstado == EstadoAnimal.FALLECIDO;
            default:
                return false;
        }
    }

    public void cambiarEstado(EstadoAnimal nuevoEstado, String usuario) {
        if (!validarTransicionEstado(nuevoEstado)) {
            throw new IllegalArgumentException("Transicion de estado no valida de " + this.estado + " a " + nuevoEstado);
        }
        this.estado = nuevoEstado;
        agregarLog(nuevoEstado, usuario);
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

    private void registrarDefuncion(String causa, Date fecha, String notasVeterinario) {
        this.estado = EstadoAnimal.FALLECIDO;
        this.fechaDefuncion = fecha;
        this.causaFallecimiento = causa;
        this.notasVeterinario = notasVeterinario;
        this.sede = null;
        if (logsEstado == null) {
            logsEstado = new ArrayList<>();
        }
        LogEstadoAnimal log = new LogEstadoAnimal();
        log.setFechaCambio(fecha);
        log.setNuevoEstado(EstadoAnimal.FALLECIDO);
        log.setUsuarioAsigno(null);
        logsEstado.add(log);
    }
}
