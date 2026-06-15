package com.wao.WAO.cuadrosmando;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

import com.wao.WAO.modelo.*;
import com.wao.WAO.modelo.enums.*;

@View(members = "metricasPoblacion; metricasAdopciones; animalesFiltrados")
public class CuadroMando {

    @LargeDisplay(icon = "paw")
    public MetricaPoblacion getMetricasPoblacion() {
        return calcularMetricasPoblacion(null, null, null);
    }

    @LargeDisplay(icon = "heart")
    public MetricaAdopciones getMetricasAdopciones() {
        return calcularMetricasAdopciones();
    }

    @SimpleList
    @ListProperties("sede.nombre, nombre, especie, raza, estado, fechaRescate")
    public Collection<Animal> getAnimalesFiltrados() {
        return aplicarFiltros(null, null, null);
    }

    public MetricaPoblacion calcularMetricasPoblacion(Sede sede, String especie, EstadoAnimal estado) {
        List<Animal> animales = aplicarFiltros(sede, especie, estado);

        int total = animales.size();
        int rescatados = (int) animales.stream().filter(a -> a.getEstado() == EstadoAnimal.RESCATADO).count();
        int enObservacion = (int) animales.stream().filter(a -> a.getEstado() == EstadoAnimal.EN_OBSERVACION).count();
        int listosAdopcion = (int) animales.stream().filter(a -> a.getEstado() == EstadoAnimal.LISTO_PARA_ADOPCION).count();
        int adoptados = (int) animales.stream().filter(a -> a.getEstado() == EstadoAnimal.ADOPTADO).count();
        int fallecidos = (int) animales.stream().filter(a -> a.getEstado() == EstadoAnimal.FALLECIDO).count();

        int ocupacionActual = total - adoptados - fallecidos;
        int capacidadTotal = 0;
        if (sede != null) {
            capacidadTotal = sede.getCapacidadMaxima();
        } else {
            List<Sede> sedes = XPersistence.getManager()
                .createQuery("SELECT s FROM Sede s", Sede.class)
                .getResultList();
            capacidadTotal = sedes.stream().mapToInt(Sede::getCapacidadMaxima).sum();
        }

        return new MetricaPoblacion(total, rescatados, enObservacion, listosAdopcion, adoptados, fallecidos, ocupacionActual, capacidadTotal);
    }

    public MetricaAdopciones calcularMetricasAdopciones() {
        List<ContratoAdopcion> contratos = XPersistence.getManager()
            .createQuery("SELECT c FROM ContratoAdopcion c", ContratoAdopcion.class)
            .getResultList();

        int totalAdopciones = contratos.size();
        int adopcionesExitosas = (int) contratos.stream()
            .filter(c -> c.getAnimal() != null && c.getAnimal().getEstado() == EstadoAnimal.ADOPTADO)
            .count();
        int seguimientosPendientes = (int) contratos.stream()
            .filter(c -> c.getSeguimientos() != null)
            .flatMap(c -> c.getSeguimientos().stream())
            .filter(s -> s.getProximaFechaSeguimiento() != null && s.getProximaFechaSeguimiento().after(new Date()))
            .count();

        BigDecimal porcentajeExito = BigDecimal.ZERO;
        if (totalAdopciones > 0) {
            porcentajeExito = BigDecimal.valueOf(adopcionesExitosas * 100.0 / totalAdopciones);
        }

        return new MetricaAdopciones(totalAdopciones, adopcionesExitosas, seguimientosPendientes, porcentajeExito);
    }

    public int calcularPorcentajeOcupacion(MetricaPoblacion mp) {
        return mp.calcularPorcentajeOcupacion();
    }

    public BigDecimal calcularPorcentajeExitoAdopciones(MetricaAdopciones ma) {
        return ma.calcularPorcentajeExitoAdopciones();
    }

    public List<Animal> aplicarFiltros(Sede sede, String especie, EstadoAnimal estado) {
        StringBuilder jpql = new StringBuilder("SELECT a FROM Animal a WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if (sede != null) {
            jpql.append(" AND a.sede.id = :sedeId");
            params.put("sedeId", sede.getId());
        }
        if (especie != null && !especie.isEmpty()) {
            jpql.append(" AND a.especie = :especie");
            params.put("especie", especie);
        }
        if (estado != null) {
            jpql.append(" AND a.estado = :estado");
            params.put("estado", estado);
        }

        TypedQuery<Animal> query = XPersistence.getManager()
            .createQuery(jpql.toString(), Animal.class);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getResultList();
    }
}
