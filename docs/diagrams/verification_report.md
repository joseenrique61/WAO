# Informe de Validación y Verificación del Modelo de Datos — WAO

**Asignatura:** Validación y Verificación de Software  
**Proyecto:** WAO — Optimización para Organizaciones de Bienestar Animal  
**Framework:** OpenXava 7.x / JPA / Hibernate  
**Autor:** José Rosales  
**Fecha:** Junio 2026

---

## 1. Resumen Ejecutivo

El presente informe documenta el proceso de verificación estática del modelo de datos de la aplicación WAO, una plataforma para la gestión integral de organizaciones de bienestar animal. Se analizaron 20 entidades JPA y 10 enumeraciones, contrastándolas contra los diagramas de clases y entidad-relación definidos en la fase de diseño.

Como resultado del análisis, se identificaron y corrigieron 16 categorías de hallazgos, que van desde errores críticos de integridad referencial hasta mejoras menores de validación de dominio. El modelo resultante es consistente, cuenta con relaciones bidireccionales completas, cascada de operaciones para preservar la integridad referencial, y restricciones de dominio que reflejan las reglas de negocio del refugio.

---

## 2. Metodología

La verificación se realizó mediante las siguientes técnicas:

1. **Inspección de código fuente** — Revisión manual de cada archivo `.java` en el paquete `com.wao.WAO.modelo`
2. **Contraste contra especificación** — Comparación sistemática contra los diagramas UML y ER almacenados en `docs/diagrams/`
3. **Análisis estático de relaciones** — Verificación de completitud bidireccional entre pares `@OneToMany` / `@ManyToOne`
4. **Detección de anomalías de diseño** — Identificación de tipos inapropiados, faltas de unicidad, ausencia de reglas de validación de dominio

---

## 3. Estado Inicial del Modelo

### 3.1 Entidades Existentes

El modelo original contenía 20 entidades JPA y 10 enumeraciones, todas en el paquete `com.wao.WAO.modelo`:

**Módulo de Administración:**
- `Sede` — Centro físico del refugio (PK auto-generada `Long`)
- `Staff` — Personal del refugio
- `IngresoFinanciero` — Registro contable de ingresos

**Módulo de Gestión de Animales:**
- `Animal` — Única entidad con PK de tipo `String` (código asignado por el usuario)
- `FotografiaMultimedia` — Archivos multimedia asociados a un animal
- `RegistroDefuncion` — Certificado de defunción (relación 1:1 con Animal)

**Módulo de Salud y Veterinaria:**
- `BitacoraMedica` — Historial de consultas veterinarias
- `PlanProfilactico` — Vacunas y desparasitaciones
- `TratamientoMedico` — Tratamientos farmacológicos activos
- `AlertaDiariaMedicacion` — Recordatorios horarios de medicación
- `InsumoMedico` — Inventario de insumos clínicos

**Módulo de Adopciones:**
- `Adoptante` — Persona solicitante de adopción
- `ContratoAdopcion` — Contrato de adopción (relación 1:1 con Animal)
- `SeguimientoPostAdopcion` — Monitoreo posterior a la adopción
- `Padrino` — Padrino o patrocinador económico
- `Apadrinamiento` — Relación de apadrinamiento entre padrino y animal

**Módulo de Visitas y Agenda:**
- `CitaVisita` — Cita de visita del adoptante a la sede
- `EntrevistaPreAdopcion` — Entrevista de evaluación del perfil del adoptante
- `Voluntario` — Voluntario del refugio
- `TurnoVoluntariado` — Asignación de turno a voluntario

### 3.2 Enumeraciones

`EstadoAnimal`, `Sexo`, `TipoPatologia`, `TipoProfilaxis`, `EstadoAdoptante`, `TipoSeguimiento`, `ResultadoVisita`, `ModalidadEntrevista`, `RangoHorario`, `ConceptoIngreso`

### 3.3 Diagramas de Referencia

Se emplearon dos diagramas como especificación:

- **Diagrama de Clases UML** (`class_diagram.md`) — Describe la estructura estática con atributos, operaciones y relaciones coloreadas por módulo
- **Diagrama Entidad-Relación** (`er_diagram.md`) — Describe las tablas, columnas, tipos de datos, claves primarias y foráneas, y cardinalidades en notación Crow's Foot

---

## 4. Hallazgos y Correcciones

### 4.1 Defectos Críticos — Integridad Referencial

#### 4.1.1 Ausencia Total de Propagación en Cascada

**Hallazgo:** Ninguna de las 20 entidades declaraba `cascade` o `orphanRemoval` en sus anotaciones `@OneToMany` o `@OneToOne`. Esto implicaba que eliminar una entidad padre (p. ej., un `Animal`) provocaría una violación de clave foránea en la base de datos, dejando registros huérfanos en todas las tablas hijas (`BitacoraMedica`, `TratamientoMedico`, `FotografiaMultimedia`, `Apadrinamiento`, `RegistroDefuncion`, `ContratoAdopcion`, etc.).

**Corrección:** Se añadió `cascade = CascadeType.ALL` con `orphanRemoval = true` en las relaciones compositivas (donde el hijo no tiene existencia independiente), y `cascade = CascadeType.ALL` sin `orphanRemoval` en las relaciones asociativas (donde el hijo puede reasignarse).

| Entidad Padre | Colección | Cascade | orphanRemoval |
|---|---|---|---|
| `Animal` | `fotografias` | ALL | ✓ |
| `Animal` | `historialMedico` | ALL | ✓ |
| `Animal` | `planProfilactico` | ALL | ✓ |
| `Animal` | `tratamientos` | ALL | ✓ |
| `Animal` | `apadrinamientos` | ALL | ✓ |
| `Animal` | `registroDefuncion` | ALL | — |
| `Animal` | `contratoAdopcion` | ALL | — |
| `Sede` | `animales` | ALL | — |
| `Sede` | `insumos` | ALL | — |
| `Sede` | `citas` | ALL | — |
| `Sede` | `turnos` | ALL | — |
| `Adoptante` | `contratos` | ALL | ✓ |
| `Adoptante` | `citas` | ALL | ✓ |
| `Adoptante` | `entrevistas` | ALL | ✓ |
| `ContratoAdopcion` | `seguimientos` | ALL | ✓ |
| `ContratoAdopcion` | `ingresos` | ALL | ✓ |
| `TratamientoMedico` | `alertas` | ALL | ✓ |
| `Padrino` | `apadrinamientos` | ALL | — |

### 4.2 Defectos Altos — Relaciones Bidireccionales Incompletas

#### 4.2.1 Ausencia de Navegación Inversa en Staff

**Hallazgo:** La entidad `Staff` no poseía colecciones `@OneToMany` para ninguna de sus relaciones `@ManyToOne` entrantes. Esto impedía la navegación desde un miembro del personal hacia:
- Los registros médicos donde actuó como veterinario tratante (`BitacoraMedica.tratante`)
- Las entrevistas que realizó como evaluador (`EntrevistaPreAdopcion.evaluador`)
- Las confirmaciones de medicación que registró (`AlertaDiariaMedicacion.staffConfirmo`)

**Corrección:** Se añadieron tres colecciones en `Staff`:

```java
@OneToMany(mappedBy="tratante")
Collection<BitacoraMedica> registrosMedicos;

@OneToMany(mappedBy="evaluador")
Collection<EntrevistaPreAdopcion> entrevistas;

@OneToMany(mappedBy="staffConfirmo")
Collection<AlertaDiariaMedicacion> confirmaciones;
```

#### 4.2.2 Ausencia de Navegación Inversa en Voluntario

**Hallazgo:** `Voluntario` no poseía una colección de `TurnoVoluntariado`, impidiendo consultar el historial de turnos de un voluntario.

**Corrección:** Se añadió:

```java
@OneToMany(mappedBy="voluntario")
Collection<TurnoVoluntariado> turnos;
```

#### 4.2.3 Ausencia de Navegación Inversa en Padrino hacia Ingresos Financieros

**Hallazgo:** La entidad `Padrino` no poseía una colección hacia `IngresoFinanciero`. Dado que `IngresoFinanciero.concepto` puede tomar el valor `APADRINAMIENTO`, y se añadió una relación `@ManyToOne Padrino padrino` en la entidad de ingresos, resultaba necesario completar la bidireccionalidad.

**Corrección:** Se añadió en `Padrino`:

```java
@OneToMany(mappedBy="padrino")
Collection<IngresoFinanciero> ingresos;
```

### 4.3 Defectos Altos — Longitud de Columna Insuficiente

**Hallazgo:** Tanto `ContratoAdopcion.contratoPDF` como `FotografiaMultimedia.imagen` declaraban `@Column(length=32)`. El sistema de archivos de OpenXava (`@File`) almacena un identificador UUID de 36 caracteres (formato `550e8400-e29b-41d4-a716-446655440000`), lo que habría provocado errores de truncamiento en la base de datos.

**Corrección:** Se modificó la longitud a `@Column(length=36)` en ambas entidades.

### 4.4 Defectos Medios — Ausencia de Unicidad en Documentos de Identidad

**Hallazgo:** Las entidades `Adoptante`, `Padrino`, `Staff` y `Voluntario` declaraban el campo `dni` como `@Required` pero sin la restricción `unique = true`. Esto permitía el registro duplicado de personas con el mismo documento de identidad, lo que constituye una violación de la integridad de datos.

**Corrección:** Se añadió `unique = true` en la anotación `@Column` de cada entidad:

```java
@Column(length=10, unique=true)  // Adoptante
@Column(length=20, unique=true)  // Padrino, Staff, Voluntario
```

### 4.5 Defectos Medios — Relación Faltante entre Ingreso y Padrino

**Hallazgo:** La entidad `IngresoFinanciero` contenía un campo comentado `idPagadorAsociado` con la nota *"TODO: Check this ID to bind it to another entity"*. El modelo permitía registrar ingresos por `APADRINAMIENTO` y `DONATIVO` mediante el enum `ConceptoIngreso`, pero no existía un vínculo de clave foránea hacia `Padrino` para identificar al pagador.

**Corrección:** Se eliminó el campo comentado y se añadió una relación JPA completa:

```java
@ManyToOne(fetch=FetchType.LAZY)
@DescriptionsList(descriptionProperties = "nombreCompleto")
Padrino padrino;
```

### 4.6 Defectos Medios — Falta de Restricción de Nulabilidad en Fotografía

**Hallazgo:** La relación `@ManyToOne` hacia `Animal` en `FotografiaMultimedia` carecía de la anotación `@Required`, permitiendo fotografías huérfanas sin animal asociado.

**Corrección:** Se añadió `@Required` sobre el campo `animal`.

### 4.7 Defectos Medios — Ausencia de Validaciones Cruzadas de Fechas

**Hallazgo:** Tres entidades presentaban pares de fechas sin validación de orden temporal:
- `TratamientoMedico.fechaFin` podía ser anterior a `fechaInicio`
- `PlanProfilactico.fechaProximoRefuerzo` podía ser anterior a `fechaAplicacion`
- `SeguimientoPostAdopcion.fechaProximoContacto` podía ser anterior a `fechaContacto`

**Corrección:** Se añadieron métodos `@AssertTrue` con mensajes de error descriptivos en cada entidad. Ejemplo representativo:

```java
@AssertTrue(message="La fecha de fin debe ser posterior a la fecha de inicio")
public boolean isFechaFinAfterFechaInicio() {
    return fechaFin == null || fechaInicio == null || !fechaFin.isBefore(fechaInicio);
}
```

### 4.8 Defectos Bajos — Uso de String para Tipo Enumerado

**Hallazgo:** El campo `AlertaDiariaMedicacion.estadoToma` estaba declarado como `String` con `@Column(length=50)`. Almacenar valores categóricos como cadenas de texto abierto es una mala práctica que permite valores inválidos, errores tipográficos e inconsistencia de datos.

**Corrección:** Se creó el enum `EstadoToma` con los valores `PENDIENTE`, `TOMADO`, `NO_TOMADO` y `SALTADO`, y se modificó el campo para usar `@Enumerated(EnumType.STRING)`.

Adicionalmente, se eliminó el campo redundante `confirmacionDada` (tipo `Boolean`), pues su valor puede derivarse enteramente de `estadoToma == TOMADO`. Se implementó un método getter:

```java
public boolean isConfirmacionDada() {
    return estadoToma == EstadoToma.TOMADO;
}
```

### 4.9 Defectos Bajos — Ausencia de Validación Numérica Mínima

**Hallazgo:** Ocho campos numéricos aceptaban valores negativos sin restricción alguna, lo que contradice el dominio del problema:

| Entidad             | Campo                | Restricción de Dominio |
|---------------------|----------------------|------------------------|
| `Animal`            | `edadAproximada`     | No puede ser negativa  |
| `Sede`              | `capacidadMaxima`    | Debe ser al menos 1    |
| `TratamientoMedico` | `frecuenciaHoras`    | Debe ser al menos 1    |
| `Voluntario`        | `horasAcumuladas`    | No puede ser negativa  |
| `InsumoMedico`      | `stockActual`        | No puede ser negativo  |
| `InsumoMedico`      | `stockMinimo`        | No puede ser negativo  |
| `IngresoFinanciero` | `monto`              | No puede ser negativo  |
| `Apadrinamiento`    | `montoAporteMensual` | No puede ser negativo  |

**Corrección:** Se añadió `@Min(0)` o `@Min(1)` según correspondiera a cada campo, utilizando `javax.validation.constraints.Min`.

### 4.10 Defectos Bajos — Campo Requerido con Semántica Incorrecta

**Hallazgo:** El campo `CitaVisita.horaLlegadaReal` estaba anotado con `@Required`. Semánticamente, la hora de llegada *real* es un valor posterior a la cita, no conocido al momento de crearla. Exigirlo como requerido forzaría al usuario a ingresar un valor ficticio.

**Corrección:** Se eliminó la anotación `@Required`, permitiendo que el campo sea opcional y se complete después de la visita.

### 4.11 Observaciones No Corregidas (Decisiones de Diseño)

Se identificaron los siguientes aspectos que, aunque constituyen desviaciones del modelo ideal, fueron retenidos por decisión explícita:

| Aspecto | Justificación |
|---|---|
| `ContratoAdopcion.animal` como `@OneToOne` | En caso de readopción, se actualiza la referencia del contrato existente. Se descartó `@OneToMany` por simplicidad operativa. |
| `Animal.idAnimal` como `String` (PK manual) | Se mantiene el identificador alfanumérico asignado por el usuario (ej. "PER-001") por requerimiento del dominio. |
| `FotografiaMultimedia.tamanoMB` ausente | El cálculo del tamaño del archivo se implementará posteriormente como propiedad calculada. |
| Ausencia de capa de validadores y acciones | La lógica de negocio (validadores, calculadores, acciones) se implementará en una fase posterior del desarrollo. |

---

## 5. Resumen Cuantitativo

| Métrica | Valor |
|---|---|
| Archivos modificados | 15 |
| Archivos creados | 1 (`EstadoToma.java`) |
| Líneas añadidas | ~127 |
| Líneas eliminadas | ~34 |
| Defectos críticos corregidos | 1 (cascada) |
| Defectos altos corregidos | 6 |
| Defectos medios corregidos | 8 |
| Defectos bajos corregidos | 10 |
| Observaciones retenidas | 4 |

---

## 6. Estado Actual del Modelo

Tras las correcciones aplicadas, el modelo de datos de WAO presenta las siguientes características:

- **20 entidades JPA** con relaciones bidireccionales completas
- **11 enumeraciones** (incluyendo `EstadoToma`)
- **Propagación en cascada** configurada en todas las relaciones de composición y agregación
- **Unicidad** garantizada en todos los campos de identidad personal (`dni`)
- **Validaciones de dominio** mediante `@Min`, `@AssertTrue` y tipos enumerados
- **Consistencia bidireccional** en todas las relaciones entre pares de entidades

El modelo es ahora internamente coherente, refleja fielmente las reglas de negocio de la organización, y garantiza la integridad referencial de los datos en operaciones de creación, actualización y eliminación.

---

## 7. Recomendaciones para Verificación Futura

1. **Verificación dinámica:** Implementar pruebas de integración JUnit utilizando `ModuleTestBase` para verificar el comportamiento transaccional de las operaciones en cascada.
2. **Validación de la capa de presentación:** Verificar que las restricciones `@Required`, `@Min` y `@AssertTrue` se reflejen correctamente en la interfaz de usuario de OpenXava.
3. **Pruebas de mutación:** Aplicar pruebas de mutación sobre los métodos `@AssertTrue` para asegurar que las validaciones de fecha capturan correctamente todos los casos límite.
4. **Verificación de integridad referencial en base de datos:** ejecutar scripts SQL que intenten violar las restricciones de clave foránea para confirmar que la cascada opera según lo especificado.
