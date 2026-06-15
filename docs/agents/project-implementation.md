# WAO Project Implementation Plan

**Project:** WAO - Animal Shelter Management System  
**Framework:** OpenXava 7.7.2 (Spanish archetype)  
**Base Package:** `com.wao.WAO`

---

## Project Context

The WAO system is an open-source platform designed to centralize the operational management of animal rescue centers. It supports multiple centers, animal registration, health records, adopter management, and adoption tracking.

### Common Structure

| Item | Location                                    |
|------|---------------------------------------------|
| Models | `src/main/java/com/wao/WAO/modelo/`         |
| Enums | `src/main/java/com/wao/WAO/modelo/enums/`   |
| Actions | `src/main/java/com/wao/WAO/acciones/`       |
| Tests | `src/test/java/com.wao.WAO.pruebas/`        |
| Controllers | `src/main/resources/xava/controladores.xml` |

### Test Command
```bash
mvn test
```

---

## Shared Enums (to create in `com.wao.WAO.modelo.enums`)

| Enum | Values |
|------|--------|
| `EstadoAnimal` | RESCATADO, EN_OBSERVACION, LISTO_PARA_ADOPCION, ADOPTADO, FALLECIDO |
| `TipoEntradaClinica` | ENFERMEDAD_CRONICA, ENFERMEDAD_TEMPORAL, PROCEDIMIENTO |
| `TipoProfilactico` | VACUNA, DESPARASITANTE |
| `EstadoPerfil` | PENDIENTE, APTO, NO_APTO |
| `ModalidadEntrevista` | PRESENCIAL, VIDEOLLAMADA |
| `TipoContacto` | LLAMADA, VISITA_FISICA |

---

## AGENT 1: Sede

### Model Location
`src/main/java/com/wao/WAO/modelo/Sede.java`

### Model Definition
```java
@Entity
@Getter @Setter
public class Sede {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    int id;
    
    @Column(length=100)
    @Required
    String nombre;
    
    @Column(length=200)
    @Required
    String direccion;
    
    int capacidadMaxima;
    
    @Column(length=100)
    String administradorACargo;
    
    boolean activa;
    
    @ElementCollection
    @ListProperties("nombre, especie, estado")
    Collection<Animal> animales;
}
```

### Business Logic Methods

**Method:** `calcularOcupacionActual(): int`  
**Description:** Counts animals that are NOT in ADOPTADO or FALLECIDO state.  
**Returns:** Number of active animals assigned to the sede.

**Method:** `desactivarSede(): boolean`  
**Description:** Soft delete the sede. Only allowed if no active animals are assigned.  
**Returns:** `true` if successful, `false` if animal assignment blocks the operation.  
**Logic:** Check if any animal has estado different from ADOPTADO or FALLECIDO. If none exist, set `activa = false` and return `true`.

### Controller XML
```xml
<controlador nombre="Sede">
    <hereda-de controlador="Typical"/>
    <accion nombre="desactivarSede" modo="detail" clase="com.wao.WAO.acciones.DesactivarSede"/>
</controlador>
```

### Action Class
`DesactivarSede extends ViewBaseAction`  
**Logic:** Call `sede.desactivarSede()`, show success/error message.

### Tests
```java
public class SedeTest extends ModuleTestBase {
    public void testCalcularOcupacionActual() throws Exception {
        login("admin", "admin");
        // Test that occupation calculation returns correct count
    }
    
    public void testDesactivarSedeConAnimalesActivos() throws Exception {
        login("admin", "admin");
        // Test that deactivation fails when active animals exist
    }
}
```

---

## AGENT 2: Animal + LogEstadoAnimal

### Model Location
`src/main/java/com/wao/WAO/modelo/Animal.java`  
`src/main/java/com/wao/WAO/modelo/LogEstadoAnimal.java`

### Model: Animal
```java
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
    
    @ElementCollection
    @ListProperties("fechaCambio, nuevoEstado, usuarioAsigno")
    Collection<LogEstadoAnimal> logsEstado;
}
```

### Model: LogEstadoAnimal
```java
@Embeddable
@Getter @Setter
public class LogEstadoAnimal {
    @Required
    Date fechaCambio;
    
    @Enumerated(EnumType.STRING)
    @Column(length=30)
    @Required
    EstadoAnimal nuevoEstado;
    
    @Column(length=100)
    String usuarioAsigno;
}
```

### Business Logic Methods

**Method:** `validarTransicionEstado(EstadoAnimal nuevoEstado): boolean`  
**Description:** Validates if the state transition is allowed.  
**Valid Transitions:**

| Current State | Allowed Next States |
|--------------|---------------------|
| RESCATADO | EN_OBSERVACION, FALLECIDO |
| EN_OBSERVACION | LISTO_PARA_ADOPCION, RESCATADO, FALLECIDO |
| LISTO_PARA_ADOPCION | ADOPTADO, EN_OBSERVACION, FALLECIDO |
| ADOPTADO | (none - final state) |
| FALLECIDO | (none - final state) |

**Returns:** `true` if transition is valid, `false` otherwise.

**Method:** `validarCondicionesParaAdopcion(): boolean`  
**Description:** Checks if the animal can be moved to LISTO_PARA_ADOPCION.  
**Conditions:** 
- No active serious pathologies ( EntradaClinica with tipo ENFERMEDAD_CRONICA or recent ENFERMEDAD_TEMPORAL without resolution)
- Has required vaccinations (TratamientoProfilactico with tipo VACUNA registered)

**Returns:** `true` if conditions are met, `false` otherwise.

**Method:** `cambiarEstado(EstadoAnimal nuevoEstado, String usuario): void`  
**Description:** Changes the animal state and creates a log entry.  
**Logic:** 
1. Call `validarTransicionEstado(nuevoEstado)` - throw exception if invalid
2. Update `estado` field
3. Create new `LogEstadoAnimal` with current date, new state, and usuario
4. Add to `logsEstado` collection

**Method:** `registrarDefuncion(String causa, Date fecha, String notasVeterinario): void`  
**Description:** Records animal death and unlinks from sede.  
**Logic:**
1. Set `estado = FALLECIDO`
2. Set `fechaDefuncion = fecha`
3. Set `causaFallecimiento = causa`
4. Set `notasVeterinario = notasVeterinario`
5. Set `sede = null` (unlink from sede)
6. Create log entry for FALLECIDO state

### Controller XML
```xml
<controlador nombre="Animal">
    <hereda-de controlador="Typical"/>
    <accion nombre="cambiarEstadoAnimal" modo="detail" clase="com.wao.WAO.acciones.CambiarEstadoAnimal"/>
    <accion nombre="registrarDefuncion" modo="detail" clase="com.wao.WAO.acciones.RegistrarDefuncion"/>
</controlador>
```

### Action Classes

**CambiarEstadoAnimal extends ViewBaseAction**  
**Logic:** Get estado from view, call `animal.cambiarEstado(estado, usuario)`.

**RegistrarDefuncion extends ViewBaseAction**  
**Logic:** Get causa, fecha, notas from view, call `animal.registrarDefuncion(...)`. Require double confirmation.

### Tests
```java
public class AnimalLogicTest extends ModuleTestBase {
    public void testTransicionRescatadoAEnObservacion() throws Exception {
        login("admin", "admin");
        // RESCATADO -> EN_OBSERVACION should be valid
    }
    
    public void testTransicionRescatadoAAdoptado() throws Exception {
        login("admin", "admin");
        // RESCATADO -> ADOPTADO should be invalid
    }
    
    public void testValidarCondicionesParaAdopcionSinPatologias() throws Exception {
        login("admin", "admin");
        // Animal without serious pathologies should be valid
    }
    
    public void testValidarCondicionesParaAdopcionConPatologiaActiva() throws Exception {
        login("admin", "admin");
        // Animal with active serious pathology should be invalid
    }
}
```

---

## AGENT 3: EntradaClinica

### Model Location
`src/main/java/com/wao/WAO/modelo/EntradaClinica.java`

### Model Definition
```java
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
}
```

### Business Logic Methods

**Method:** `alertarSiEsContagiosa(): boolean`  
**Description:** Checks if this entry is for a contagious pathology.  
**Returns:** `true` if `esContagiosa = true`, `false` otherwise.

**Method:** `registrarEntrada(): void`  
**Description:** Registers a clinical entry and triggers alert if contagious.  
**Logic:**
1. If `esContagiosa = true`, add warning message to the user
2. Save the entry

### Controller XML
```xml
<controlador nombre="EntradaClinica">
    <hereda-de controlador="Typical"/>
    <accion nombre="agregarEntradaClinica" modo="detail" clase="com.wao.WAO.acciones.AgregarEntradaClinica"/>
</controlador>
```

### Action Class
**AgregarEntradaClinica extends ViewBaseAction**  
**Logic:** Get all fields from view, call `entradaClinica.registrarEntrada()`.

### Tests
```java
public class EntradaClinicaLogicTest extends ModuleTestBase {
    public void testAlertaPatologiaContagiosa() throws Exception {
        login("admin", "admin");
        // When esContagiosa is true, alert should be shown
    }
    
    public void testRegistroEntradaClinica() throws Exception {
        login("admin", "admin");
        // Entry should be saved correctly
    }
}
```

---

## AGENT 4: TratamientoProfilactico

### Model Location
`src/main/java/com/wao/WAO/modelo/TratamientoProfilactico.java`

### Model Definition
```java
@Entity
@Getter @Setter
public class TratamientoProfilactico {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    int id;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @DescriptionsList
    Animal animal;
    
    @Enumerated(EnumType.STRING)
    @Column(length=30)
    @Required
    TipoProfilactico tipo;
    
    @Column(length=100)
    @Required
    String nombreProducto;
    
    @Required
    Date fechaAplicacion;
    
    Date fechaProximoRefuerzo;
}
```

### Business Logic Methods

**Method:** `validarFechaAplicacionPasada(): boolean`  
**Description:** Validates that fechaAplicacion is not in the future.  
**Logic:** Compare `fechaAplicacion` with current date.  
**Returns:** `true` if `fechaAplicacion <= today`, `false` if future date.

**Method:** `agendarRecordatorioRefuerzo(): boolean`  
**Description:** Checks if there is a next reinforcement date scheduled.  
**Returns:** `true` if `fechaProximoRefuerzo` is not null and is in the future.

### Controller XML
```xml
<controlador nombre="TratamientoProfilactico">
    <hereda-de controlador="Typical"/>
    <accion nombre="registrarTratamiento" modo="detail" clase="com.wao.WAO.acciones.RegistrarTratamiento"/>
</controlador>
```

### Action Class
**RegistrarTratamiento extends ViewBaseAction**  
**Logic:** Get fields from view, call `tratamiento.validarFechaAplicacionPasada()` - if invalid show error, otherwise save. If `agendarRecordatorioRefuerzo()` returns true, show reminder message.

### Tests
```java
public class TratamientoProfilacticoLogicTest extends ModuleTestBase {
    public void testFechaPasadaValida() throws Exception {
        login("admin", "admin");
        // Past date should be valid
    }
    
    public void testFechaFuturaInvalida() throws Exception {
        login("admin", "admin");
        // Future date should be rejected
    }
    
    public void testRecordatorioRefuerzo() throws Exception {
        login("admin", "admin");
        // Recordatorio should be scheduled when future date exists
    }
}
```

---

## AGENT 5: Adoptante

### Model Location
`src/main/java/com/wao/WAO/modelo/Adoptante.java`

### Model Definition
```java
@Entity
@Getter @Setter
public class Adoptante {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    int id;
    
    @Column(length=20)
    @Required
    String dni;
    
    @Column(length=100)
    @Required
    String nombre;
    
    @Column(length=200)
    @Required
    String direccion;
    
    @Column(length=20)
    @Required
    String telefono;
    
    @Column(length=1000)
    String detalleVivienda;
    
    boolean tieneNinos;
    boolean tieneMascotas;
    
    @Enumerated(EnumType.STRING)
    @Column(length=20)
    EstadoPerfil estadoPerfil;
}
```

### Business Logic Methods

**Method:** `validarCamposObligatoriosCompletos(): boolean`  
**Description:** Validates that all mandatory form fields are filled.  
**Mandatory Fields:** dni, nombre, direccion, telefono, detalleVivienda, tieneNinos, tieneMascotas  
**Returns:** `true` if all fields are non-null and non-empty, `false` otherwise.

**Method:** `enviarPerfilAEvaluacion(): void`  
**Description:** Changes perfil state to PENDIENTE.  
**Logic:** Set `estadoPerfil = PENDIENTE`.

**Method:** `calificarPerfil(EstadoPerfil calificacion): void`  
**Description:** Changes perfil state to APTO or NO_APTO.  
**Logic:** Set `estadoPerfil = calificacion`.

### Controller XML
```xml
<controlador nombre="Adoptante">
    <hereda-de controlador="Typical"/>
    <accion nombre="enviarPerfilAEvaluacion" modo="detail" clase="com.wao.WAO.acciones.EnviarPerfilAEvaluacion"/>
    <accion nombre="calificarPerfil" modo="detail" clase="com.wao.WAO.acciones.CalificarPerfil"/>
</controlador>
```

### Action Classes

**EnviarPerfilAEvaluacion extends ViewBaseAction**  
**Logic:** Call `adoptante.enviarPerfilAEvaluacion()`.

**CalificarPerfil extends ViewBaseAction**  
**Logic:** Get calificacion from view (APTO or NO_APTO), call `adoptante.calificarPerfil(calificacion)`.

### Tests
```java
public class AdoptanteLogicTest extends ModuleTestBase {
    public void testCamposCompletosValidos() throws Exception {
        login("admin", "admin");
        // All fields filled should return true
    }
    
    public void testCamposIncompletosInvalidos() throws Exception {
        login("admin", "admin");
        // Missing required fields should return false
    }
    
    public void testCalificarApto() throws Exception {
        login("admin", "admin");
        // State should change to APTO
    }
    
    public void testCalificarNoApto() throws Exception {
        login("admin", "admin");
        // State should change to NO_APTO
    }
}
```

---

## AGENT 6: Entrevista

### Model Location
`src/main/java/com/wao/WAO/modelo/Entrevista.java`

### Model Definition
```java
@Entity
@Getter @Setter
public class Entrevista {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    int id;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @DescriptionsList
    Adoptante adoptante;
    
    @Required
    LocalDateTime fechaHora;
    
    @Enumerated(EnumType.STRING)
    @Column(length=30)
    @Required
    ModalidadEntrevista modalidad;
    
    @Column(length=255)
    String enlaceReunion;
    
    @Column(length=100)
    String evaluador;
}
```

### Business Logic Methods

**Method:** `validarAdoptanteAptoParaEntrevista(): boolean`  
**Description:** Checks if the adoptante has all required form fields completed.  
**Logic:** Call `adoptante.validarCamposObligatoriosCompletos()`.  
**Returns:** `true` if adoptante is ready for interview scheduling.

**Method:** `validarDisponibilidadEvaluador(String evaluador, LocalDateTime fechaHora): boolean`  
**Description:** Checks if the evaluator has no overlapping interviews at the same time.  
**Logic:** Query existing Entrevista records for the same evaluador where fechaHora overlaps (same hour).  
**Returns:** `true` if no overlap exists, `false` if scheduling conflict.

### Controller XML
```xml
<controlador nombre="Entrevista">
    <hereda-de controlador="Typical"/>
    <accion nombre="agendarEntrevista" modo="detail" clase="com.wao.WAO.acciones.AgendarEntrevista"/>
</controlador>
```

### Action Class
**AgendarEntrevista extends ViewBaseAction**  
**Logic:** 
1. Get adoptante from view, call `entrevista.validarAdoptanteAptoParaEntrevista()` - if false show error
2. Get evaluador and fechaHora, call `entrevista.validarDisponibilidadEvaluador(...)` - if false show error
3. Save the interview

### Tests
```java
public class EntrevistaLogicTest extends ModuleTestBase {
    public void testAdoptanteCompletoPuedeAgendar() throws Exception {
        login("admin", "admin");
        // Adoptante with complete fields should pass validation
    }
    
    public void testAdoptanteIncompletoNoPuedeAgendar() throws Exception {
        login("admin", "admin");
        // Adoptante with incomplete fields should fail validation
    }
    
    public void testHorarioLibreValido() throws Exception {
        login("admin", "admin");
        // No overlap should mean valid scheduling
    }
    
    public void testHorarioSolapadoInvalido() throws Exception {
        login("admin", "admin");
        // Overlapping time should fail validation
    }
}
```

---

## AGENT 7: ContratoAdopcion + SeguimientoPostAdopcion

### Model Location
`src/main/java/com/wao/WAO/modelo/ContratoAdopcion.java`  
`src/main/java/com/wao/WAO/modelo/SeguimientoPostAdopcion.java`

### Model: ContratoAdopcion
```java
@Entity
@Getter @Setter
public class ContratoAdopcion {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    int id;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @DescriptionsList
    Adoptante adoptante;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @DescriptionsList
    Animal animal;
    
    @Required
    Date fechaAdopcion;
    
    @Column(length=100)
    String responsableCentro;
    
    @ElementCollection
    @ListProperties("fechaContacto, notasEstado, tipoContacto")
    Collection<SeguimientoPostAdopcion> seguimientos;
}
```

### Model: SeguimientoPostAdopcion
```java
@Embeddable
@Getter @Setter
public class SeguimientoPostAdopcion {
    @Required
    Date fechaContacto;
    
    @Column(length=1000)
    String notasEstado;
    
    @Enumerated(EnumType.STRING)
    @Column(length=30)
    TipoContacto tipoContacto;
    
    Date proximaFechaSeguimiento;
}
```

### Business Logic Methods

**Method:** `validarCompatibilidad(): boolean`  
**Description:** Validates that adoptante is APTO and animal is LISTO_PARA_ADOPCION.  
**Logic:** Check `adoptante.estadoPerfil == APTO` AND `animal.estado == LISTO_PARA_ADOPCION`.  
**Returns:** `true` if compatible, `false` otherwise.

**Method:** `procesarVinculacion(Adoptante adoptante, Animal animal): void`  
**Description:** Creates adoption contract and changes animal state to ADOPTADO.  
**Logic:**
1. Call `validarCompatibilidad()` - throw exception if invalid
2. Set `this.adoptante = adoptante`
3. Set `this.animal = animal`
4. Set `animal.estado = ADOPTADO`
5. Create log entry for the state change

**Method:** `validarAnimalEstaAdoptado(): boolean`  
**Description:** Checks if the linked animal is in ADOPTADO state.  
**Logic:** Check `animal.estado == ADOPTADO`.  
**Returns:** `true` if animal is adopted, `false` otherwise.

**Method:** `registrarSeguimiento(Date fechaContacto, String notasEstado, TipoContacto tipoContacto, Date proximaFecha): void`  
**Description:** Registers a post-adoption follow-up.  
**Logic:**
1. Call `validarAnimalEstaAdoptado()` - throw exception if animal not adopted
2. Create new `SeguimientoPostAdopcion` with provided data
3. Add to `seguimientos` collection

### Controller XML
```xml
<controlador nombre="ContratoAdopcion">
    <hereda-de controlador="Typical"/>
    <accion nombre="procesarVinculacion" modo="detail" clase="com.wao.WAO.acciones.ProcesarVinculacion"/>
    <accion nombre="registrarSeguimiento" modo="detail" clase="com.wao.WAO.acciones.RegistrarSeguimiento"/>
</controlador>
```

### Action Classes

**ProcesarVinculacion extends ViewBaseAction**  
**Logic:** Get adoptante and animal from view, call `contrato.procesarVinculacion(adoptante, animal)`.

**RegistrarSeguimiento extends ViewBaseAction**  
**Logic:** Get fields from view, call `contrato.registrarSeguimiento(...)`.

### Tests
```java
public class ContratoAdopcionLogicTest extends ModuleTestBase {
    public void testCompatibilidadAptoYListo() throws Exception {
        login("admin", "admin");
        // Adoptante APTO + Animal LISTO should be valid
    }
    
    public void testCompatibilidadNoApto() throws Exception {
        login("admin", "admin");
        // Adoptante NO_APTO should be invalid
    }
    
    public void testCompatibilidadPendiente() throws Exception {
        login("admin", "admin");
        // Adoptante PENDIENTE should be invalid
    }
    
    public void testCompatibilidadAnimalNoListo() throws Exception {
        login("admin", "admin");
        // Animal not LISTO should be invalid
    }
    
    public void testVinculacionCambiaEstadoAAdoptado() throws Exception {
        login("admin", "admin");
        // After vinculacion, animal state should be ADOPTADO
    }
    
    public void testSeguimientoSoloAdoptados() throws Exception {
        login("admin", "admin");
        // Follow-up only allowed for adopted animals
    }
}
```

---

## AGENT 8: CuadroMando (Dashboard)

### Model Location
`src/main/java/com/wao/WAO/cuadrosmando/CuadroMando.java`

### Model: MetricaPoblacion
```java
@Entity
@Getter @Setter
public class MetricaPoblacion {
    int totalAnimales;
    int rescatados;
    int enObservacion;
    int listosAdopcion;
    int adoptados;
    int fallecidos;
    int ocupacionActual;
    int capacidadTotal;
}
```

### Model: MetricaAdopciones
```java
@Entity
@Getter @Setter
public class MetricaAdopciones {
    int totalAdopciones;
    int adopcionesExitosas;
    int seguimientosPendientes;
    BigDecimal porcentajeExito;
}
```

### Dashboard Class
```java
package com.wao.WAO.cuadrosmando;

public class CuadroMando extends ModuleTestBase {
    public CuadroMando(String testName) {
        super(testName, "WAO", "CuadroMando");
    }
}
```

### Business Logic Methods

**Method:** `calcularMetricasPoblacion(Sede sede, String especie, EstadoAnimal estado): MetricaPoblacion`  
**Description:** Calculates population metrics with optional filters.  
**Logic:**
1. Query Animal entities filtered by sede, especie, estado (if provided)
2. Count totals by estado
3. Calculate ocupacionActual from filtered sede's capacidadMaxima
4. Return filled `MetricaPoblacion`

**Method:** `calcularPorcentajeOcupacion(): int`  
**Description:** Calculates occupation percentage.  
**Logic:** `return (ocupacionActual * 100) / capacidadTotal`  
**Returns:** Percentage value (0-100).

**Method:** `calcularPorcentajeExitoAdopciones(): BigDecimal`  
**Description:** Calculates adoption success percentage.  
**Logic:** `return (adopcionesExitosas * 100) / totalAdopciones`  
**Returns:** Percentage as BigDecimal.

**Method:** `aplicarFiltros(Sede sede, String especie, EstadoAnimal estado): List<Animal>`  
**Description:** Applies filters to animal list and returns filtered results.  
**Logic:** Build dynamic query based on non-null filter parameters.  
**Returns:** List of matching Animal entities.

### Controller XML
```xml
<controlador nombre="CuadroMando">
    <hereda-de controlador="Typical"/>
    <accion nombre="actualizarDashboard" modo="detail" clase="com.wao.WAO.acciones.ActualizarDashboard"/>
</controlador>
```

### Action Class
**ActualizarDashboard extends ViewBaseAction**  
**Logic:** Get filter values from view, call dashboard methods to refresh metrics display.

### Tests
```java
public class CuadroMandoLogicTest extends ModuleTestBase {
    public void testCalcularMetricasPoblacionTotal() throws Exception {
        login("admin", "admin");
        // Total count should match sum of all estados
    }
    
    public void testPorcentajeOcupacion() throws Exception {
        login("admin", "admin");
        // Percentage = ocupacionActual / capacidadTotal * 100
    }
    
    public void testPorcentajeExitoAdopciones() throws Exception {
        login("admin", "admin");
        // Percentage = adopcionesExitosas / totalAdopciones * 100
    }
    
    public void testFiltroPorSede() throws Exception {
        login("admin", "admin");
        // Metrics should only include animals from selected sede
    }
}
```

---

## Conventions (Mandatory for All Agents)

### Model Conventions
- Use `@Getter @Setter` Lombok annotations at class level
- Fields **without visibility modifier** (package-level), NOT `private`
- Do NOT write manual getters/setters
- Master-detail collections: `@ElementCollection` + `@Embeddable`
- Use `@ManyToOne(fetch=FetchType.LAZY)` for references

### Controller Conventions
- Extend `Typical` controller with `<hereda-de controlador="Typical"/>`
- Controller name must match entity name
- Actions extend `ViewBaseAction`

### Test Conventions
- JUnit 4
- Extend `ModuleTestBase`
- First line of every test method: `login("admin", "admin");`
- Location: `src/main/java/com/wao/WAO/pruebas/`

### Action Conventions
- Actions are in `com.wao.WAO.acciones`
- Actions only call model methods - NO business logic in actions
- All business logic implemented in model methods

---

## File Manifest by Agent

| Agent | Files to Create |
|-------|-----------------|
| 1 | `modelo/Sede.java`, `acciones/DesactivarSede.java`, `pruebas/SedeTest.java` |
| 2 | `modelo/enums/EstadoAnimal.java`, `modelo/Animal.java`, `modelo/LogEstadoAnimal.java`, `acciones/CambiarEstadoAnimal.java`, `acciones/RegistrarDefuncion.java`, `pruebas/AnimalLogicTest.java` |
| 3 | `modelo/enums/TipoEntradaClinica.java`, `modelo/EntradaClinica.java`, `acciones/AgregarEntradaClinica.java`, `pruebas/EntradaClinicaLogicTest.java` |
| 4 | `modelo/enums/TipoProfilactico.java`, `modelo/TratamientoProfilactico.java`, `acciones/RegistrarTratamiento.java`, `pruebas/TratamientoProfilacticoLogicTest.java` |
| 5 | `modelo/enums/EstadoPerfil.java`, `modelo/Adoptante.java`, `acciones/EnviarPerfilAEvaluacion.java`, `acciones/CalificarPerfil.java`, `pruebas/AdoptanteLogicTest.java` |
| 6 | `modelo/enums/ModalidadEntrevista.java`, `modelo/Entrevista.java`, `acciones/AgendarEntrevista.java`, `pruebas/EntrevistaLogicTest.java` |
| 7 | `modelo/enums/TipoContacto.java`, `modelo/ContratoAdopcion.java`, `modelo/SeguimientoPostAdopcion.java`, `acciones/ProcesarVinculacion.java`, `acciones/RegistrarSeguimiento.java`, `pruebas/ContratoAdopcionLogicTest.java` |
| 8 | `cuadrosmando/CuadroMando.java`, `modelo/MetricaPoblacion.java`, `modelo/MetricaAdopciones.java`, `acciones/ActualizarDashboard.java`, `pruebas/CuadroMandoLogicTest.java` |

### Also Required
- `src/main/resources/xava/controladores.xml` - Register all controllers (Agent 1 can create initial structure)

---

## Cross-Agent Dependencies

| Agent | Dependencies |
|-------|-------------|
| Animal | Requires Sede entity |
| EntradaClinica | Requires Animal entity |
| TratamientoProfilactico | Requires Animal entity |
| Entrevista | Requires Adoptante entity |
| ContratoAdopcion | Requires Adoptante and Animal entities |
| CuadroMando | Reads from all entities |

**Note:** Agents work in parallel. Only `mvn test` execution order needs to consider FK relationships. Run Agent 1 first (Sede), then Agents 2-7 in any order, then Agent 8 last (CuadroMando reads data).

---

## Validation Rules Summary

| Requirement | Validation |
|-------------|------------|
| RF-02 | Animal cannot go to LISTO_PARA_ADOPCION with active serious pathologies or without required vaccinations |
| RF-03 | Death registration is irreversible, requires double confirmation |
| RF-05 | Application date must not be in the future |
| RF-07 | Reject vinculacion if animal not LISTO_PARA_ADOPCION or adoptante not APTO |
| RF-09 | Only schedule interview if adoptante has all required form fields complete |
| RF-10 | Sede with assigned animals cannot be deleted, only deactivated |

---

## Final Verification Checklist

- [ ] All enums created in `com.wao.WAO.modelo.enums`
- [ ] All models created with correct annotations
- [ ] All business logic in model methods
- [ ] All actions call model methods only
- [ ] Controllers XML updated
- [ ] Unit tests created for all business logic
- [ ] `mvn test` passes for each module
- [ ] Lombok `@Getter @Setter` at class level
- [ ] Package-level fields (no `private`)
- [ ] `@ElementCollection` + `@Embeddable` for master-detail collections