package com.wao.WAO.cuadrosmando;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import javax.persistence.*;

import com.wao.WAO.modelo.dashboardDtos.FechasAnimalYContratoDTO;
import lombok.Getter;
import lombok.Setter;
import org.openxava.annotations.*;

import com.wao.WAO.modelo.*;
import com.wao.WAO.modelo.enums.*;
import org.openxava.jpa.XPersistence;

@View(members = "filtros [sede, especie, estado]; metricas [#totalDeAnimales, porcentajeDeOcupacion; porcentajeDeAdopcionExitosa, listosParaAdopcion; tiempoPromedioDeAdopcion, porcentajeDeFallecidos]")
@Getter
@Setter
public class CuadroMando {
    // Filtros
    @DescriptionsList
    @ManyToOne(fetch = FetchType.LAZY)
    Sede sede;

    String especie;

    EstadoAnimal estado;

    // Propiedades
    @Depends("sede, especie, estado")
    @LargeDisplay(icon = "paw")
    public String getPorcentajeDeOcupacion() {
        return String.format("%.2f", calcularMetricasPoblacion().calcularPorcentajeOcupacion()) + "%";
    }

    @Depends("sede, especie, estado")
    @LargeDisplay(icon = "heart")
    public String getPorcentajeDeAdopcionExitosa() {
        return String.format("%.2f", calcularMetricasAdopciones().calcularPorcentajeExitoAdopciones()) + "%";
    }

    @Depends("sede, especie, estado")
    @LargeDisplay(icon = "heart")
    public String getPorcentajeDeFallecidos() {
        return String.format("%.2f", calcularMetricasPoblacion().calcularPorcentajeFallecido()) + "%";
    }

    @Depends("sede, especie, estado")
    @LargeDisplay(icon = "heart")
    public String getListosParaAdopcion() {
        return String.format("%d", calcularMetricasPoblacion().getListosAdopcion());
    }

    @Depends("sede, especie, estado")
    @LargeDisplay(icon = "heart")
    public String getTotalDeAnimales() {
        return String.format("%d", calcularMetricasPoblacion().getTotalAnimales());
    }

    @Depends("sede, especie, estado")
    @LargeDisplay(icon = "heart")
    public String getTiempoPromedioDeAdopcion() {
        List<Animal> animales = aplicarFiltros();

        if (animales.isEmpty()) {
            return "0 días";
        }

        List<String> ids = animales.stream().map(Animal::getId).toList();

        List<FechasAnimalYContratoDTO> fechas = XPersistence.getManager()
                .createQuery("SELECT new com.wao.WAO.modelo.dashboardDtos.FechasAnimalYContratoDTO(ca.fechaAdopcion, a.fechaRescate) FROM ContratoAdopcion ca INNER JOIN Animal a ON ca.animal = a.id WHERE a.id IN :animales", FechasAnimalYContratoDTO.class)
                .setParameter("animales", ids)
                .getResultList();
        if (fechas.isEmpty()) {
            return "0 días";
        }

        double tiempoPromedio = 0;
        for (FechasAnimalYContratoDTO f : fechas) {

            LocalDate date1 = f.getFechaAdopcion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate date2 = f.getFechaRescate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            tiempoPromedio += ChronoUnit.DAYS.between(date2, date1);
        }
        tiempoPromedio /= fechas.size();

        return String.format("%.2f días", tiempoPromedio);
    }



    public MetricaPoblacion calcularMetricasPoblacion() {
        List<Animal> animales = aplicarFiltros();

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

        double porcentajeExito = 0;
        if (totalAdopciones > 0) {
            porcentajeExito = (double) adopcionesExitosas * 100.0 / totalAdopciones;
        }

        return new MetricaAdopciones(totalAdopciones, adopcionesExitosas, seguimientosPendientes, porcentajeExito);
    }

    public List<Animal> aplicarFiltros() {
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
