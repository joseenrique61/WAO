# **Especificación de Requisitos de Software \- Proyecto WAO**

**Nombre del proyecto:** WAO  
**Integrantes:** Anchapaxi Ariel, Brazales Camilo, Cadena Juan, Cerezo José, Solórzano Camily

# **1\. Introducción**

El proyecto WAO surge como una iniciativa que busca ayudar a centros de adopción. El objetivo principal es desarrollar un sistema de código abierto (open source) dedicado a la gestión integral de centros de adopción y veterinaria, específicamente diseñado para apoyar la labor de la organización PAE (Protección Animal Ecuador).

## **Perspectiva del proyecto**

La propuesta responde a la crítica situación de maltrato y abandono animal en Quito, una problemática que actualmente supera la capacidad de respuesta de las organizaciones de bienestar animal. En la actualidad, la administración de estos centros suele depender de procesos manuales o herramientas fragmentadas, lo que genera:

* Ineficiencias operativas y errores en el manejo de datos.
* Dificultades para realizar un seguimiento riguroso de la salud de los animales.
* Falta de control estricto sobre el perfil de los adoptantes.

## **Propósito**

El propósito de este documento es detallar los requisitos para el desarrollo del software, sirviendo como una guía fundamental para los equipos de desarrollo, pruebas y gestión del proyecto.

## **Alcance**

**Inclusiones**

* **Gestión Integral de Animales:** Registro detallado de cada animal que ingresa al centro, permitiendo un seguimiento desde su rescate hasta la adopción final.
* **Módulo Sanitario:** Funcionalidad para el registro de enfermedades y el control de medicinas para un manejo sanitario eficiente.
* **Control de Adopciones:** Implementación de un proceso de registro y control exhaustivo, incluyendo filtros rigurosos para evaluar los perfiles de los adoptantes y garantizar su trazabilidad.
* **Gestión de Visitas:** Capacidad para programar y gestionar las visitas de interesados al centro de adopción.
* **Administración Multi-centro:** Gestión centralizada que permite administrar múltiples ubicaciones o sedes si fuera necesario.
* **Naturaleza Open Source:** Desarrollo bajo un esquema de código abierto para fomentar la colaboración y el mantenimiento comunitario.

**Exclusiones**

* **Venta de Animales:** El sistema está diseñado estrictamente para la adopción ética; el uso para la comercialización de animales se considera un riesgo y está fuera del propósito del proyecto.
* **Infraestructura Física:** El proyecto se limita al desarrollo del software y no incluye la provisión de equipos de computación o mejoras en la infraestructura física de los centros.
* **Financiamiento de Operaciones:** No contempla la gestión de recursos económicos para el funcionamiento diario del centro, enfocándose únicamente en la herramienta de gestión operativa.

## **Definiciones, Acrónimos y Abreviaturas**

| Término | Definición |
| :---- | :---- |
| SRS | Especificación de Requisitos de Software |
| Admin | El principal usuario que interactúa con el sistema |
| Desarollador | El desarrollador principal responsable de la codificación |

## **Referencias**

Se listan todos los documentos, estándares o materiales de apoyo utilizados para la elaboración de esta especificación.

* ISO 25010
* IEEE 830

# **2\. Descripción General**

## **Perspectiva del Producto**

El sistema WAO es una plataforma independiente y de código abierto diseñada para centralizar la gestión operativa de centros de rescate animal. Aunque funciona de manera autónoma, su arquitectura permite la administración de múltiples sedes o centros de forma centralizada. El sistema busca reemplazar procesos manuales y herramientas dispersas por una solución técnica estandarizada y robusta que garantice la trazabilidad total de cada mascota.

## **Funciones del Producto**

El sistema debe realizar las siguientes funciones principales para optimizar la administración operacional:

* **Gestión de Cuentas:** Administración de la plataforma mediante un usuario operativo centralizado que maneja la configuración global del sistema.
* **Procesamiento de Datos:**
1) Registro detallado y seguimiento de animales rescatados.
2) Gestión de historias clínicas, incluyendo registro de enfermedades y administración de medicinas.
3) Evaluación y filtrado riguroso de perfiles de adoptantes.
* **Generación de Informes:** Reportes sobre el estado sanitario de la población animal, estadísticas de adopción ética y seguimiento de trazabilidad desde el rescate hasta el hogar final.

## **Características del Usuario**

Los siguientes son los tipos de usuarios y sus características relevantes para el sistema:

| Tipo de Usuario | Nivel de Experiencia | Responsabilidades Clave |
| :---- | :---- | :---- |
| Administrador | Alto | Interacción diaria con todas las funciones: registro veterinario, gestión de animales, control de filtros de adopción y administración del centro.  |

## **Restricciones**

Las siguientes condiciones limitan las opciones de desarrollo del sistema WAO:

* **Infraestructura:** El software debe adaptarse a la capacidad tecnológica y de red actual de los centros de adopción y veterinaria.
* **Modelo de Licenciamiento:** El sistema debe mantenerse bajo una filosofía **Open Source**.
* **Recursos Económicos:** El desarrollo está sujeto a las limitaciones presupuestarias del proyecto académico y de la organización receptora.
* **Plataforma:** Debe ser compatible con entornos que permitan la ejecución del "Esquema Offline" si se requiere integración local.
* **Framework de desarrollo:** OpenXava no permite la creación de roles en versiones que no sean la de pago, por lo que tener múltiples usuarios con diferentes responsabilidades no es posible.

## **Suposiciones y Dependencias**

| Tipo | Descripción |
| :---- | :---- |
| Suposición | Se asume que existe un interés constante de la ciudadanía en la adopción ética y una cultura de adopción frente a la compra.  |
| Suposición | Se asume que el sistema no será desviado para fines de comercialización de animales.  |
| Dependencia  | El sistema depende de la disponibilidad de animales rescatados por PAE u otros centros para poblar la base de datos.  |
| Dependencia  | La implementación exitosa depende de la colaboración continua entre los desarrolladores y el personal operativo de los centros.  |

# **3\. Requisitos Específicos**

Aquí se listan los requisitos funcionales y no funcionales de forma detallada, clara y verificable.

## **3.1. Requisitos Funcionales**

Estos requisitos describen lo que el sistema debe hacer agrupados por módulos.

**Módulo 1: Gestión de Animales**

| RF-01 | Registro Integral de Ingreso, Rescate y Ubicación |
| :---- | :---- |
| Descripción | Permite dar de alta a un animal consolidando su perfil, la historia de cómo fue rescatado y en qué sede se ubica.  Incluye: Registro de Animales, Historial de Rescate, Asignación de Ubicación. Prioridad: Crítica. |
| Entradas | Nombre, Especie, Raza, Edad aprox., Sexo. Fecha y lugar de rescate, Condiciones de hallazgo. Sede/Hogar temporal asignado. |
| Proceso | Validar formato de fechas; generar ID único del animal; vincular el registro a la tabla de Sedes; crear expediente base en la base de datos. |
| Salidas | Ficha principal del animal creada e ID generado. |
| Reglas / Condiciones | Los campos de Especie, Sexo y Sede son obligatorios. No se puede asignar un animal a una sede inactiva o sin capacidad. |

| RF-02 | Gestión de Estados del Animal |
| :---- | :---- |
| Descripción | Controla el flujo del estado del animal dentro del refugio para habilitarlo o no para adopción. Incluye: Gestión de Estados de Adopción. Prioridad: Alta. |
| Entradas | ID del Animal, Nuevo Estado ("Rescatado", "En Observación", "Listo para Adopción", "Adoptado"). |
| Proceso | Recibir solicitud de cambio de estado; validar permisos del usuario; actualizar el campo de estado en el expediente; registrar la fecha del cambio en el log. |
| Salidas | Estado actualizado visualmente en el sistema y catálogos. |
| Reglas / Condiciones | Un animal no puede pasar a "Listo para Adopción" si tiene patologías activas graves o si no cuenta con vacunas obligatorias. El estado "Adoptado" solo se activa automáticamente tras el RF-10 (Vinculación). |

| RF-03 | Registro de Defunciones |
| :---- | :---- |
| Descripción | Documenta el fallecimiento de animales para fines estadísticos y cierre de expedientes. Incluye: Registro de Defunciones. Prioridad: Baja. |
| Entradas | ID del Animal, Fecha de defunción, Causa de fallecimiento, Notas del veterinario. |
| Proceso | Cambiar estado del animal a "Fallecido"; desvincular de la Sede para liberar espacio; guardar la causa en el historial. |
| Salidas | Expediente cerrado; estadística de mortalidad actualizada. |
| Reglas / Condiciones | Esta acción es irreversible. Requiere confirmación de doble paso por el usuario. |

**Módulo 2: Gestión de Salud y Veterinaria**

| RF-04 | Bitácora Médica y Patologías |
| :---- | :---- |
| Descripción | Genera una línea de tiempo clínica almacenando enfermedades, consultas y procedimientos. Incluye: Registro de Patologías, Bitácora Médica. Prioridad: Crítica. |
| Entradas | ID del Animal, Fecha de consulta, Tipo (Enfermedad crónica/temporal, Procedimiento), Diagnóstico, Veterinario tratante. |
| Proceso | Almacenar el diagnóstico; ordenarlo cronológicamente en la vista del paciente; si la patología es catalogada como "Contagiosa", alertar al sistema. |
| Salidas | Nueva entrada en la historia clínica del animal. |
| Reglas / Condiciones | Solo usuarios con rol "Veterinario" o "Administrador" pueden agregar o editar entradas clínicas. |

| RF-05 | Plan Profiláctico (Vacunación y Desparasitación) |
| :---- | :---- |
| Descripción | Programa y marca como completadas las vacunas y desparasitaciones. Incluye: Calendario de Vacunación, Control de Desparasitación. Prioridad: Alta. |
| Entradas | ID del Animal, Tipo (Vacuna/Desparasitante), Nombre del producto, Fecha de aplicación, Fecha de próximo refuerzo (opcional). |
| Proceso | Registrar aplicación; si existe "Fecha de próximo refuerzo", agendar un evento en el calendario interno del sistema. |
| Salidas | Historial profiláctico actualizado y evento de recordatorio creado. |
| Reglas / Condiciones | No se puede registrar fechas futuras para aplicaciones ya realizadas (la fecha de aplicación debe ser menor que fecha actual). |

**Módulo 3: Gestión de Adoptantes y Adopciones**

| RF-06 | Registro y Perfil de Adoptantes (Filtro) |
| :---- | :---- |
| Descripción | Almacena datos del adoptante, entorno y califica su perfil. Incluye: Registro de Adoptantes, Formulario de Perfil, Sistema de Evaluación. Prioridad: Crítica. |
| Entradas | Datos personales (DNI, Nombre, Dirección, Teléfono), Respuestas del formulario (Vivienda, Niños, Mascotas), Calificación del evaluador (Apto/No Apto). |
| Proceso | Crear cuenta de adoptante; encriptar datos sensibles (DNI); almacenar cuestionario; actualizar el estado del perfil según la calificación del trabajador del centro. |
| Salidas | Perfil de adoptante estructurado con su respectiva calificación (Apto/No apto). |
| Reglas / Condiciones | Todos los campos del formulario de entorno son obligatorios para poder enviar el perfil a evaluación. |

| RF-07 | Vinculación de Adopción |
| :---- | :---- |
| Descripción | Relaciona formalmente adoptante y animal. Incluye: Vinculación de Adopción,  Prioridad: Crítica. |
| Entradas | ID del Adoptante, ID del Animal, . |
| Proceso | Validar compatibilidad (Adoptante "Apto", Animal "Listo para Adopción"); crear relación en base de datos; cambiar estado del animal a "Adoptado". |
| Salidas |  de estado ejecutado. |
| Reglas / Condiciones | El sistema rechazará la vinculación si el animal no está en estado "Listo para adopción" o si el adoptante tiene estado "No apto" o "Pendiente". |

| RF-08 | Seguimiento Post-Adopción |
| :---- | :---- |
| Descripción | Registra el control y bienestar del animal tras la entrega. Incluye: Seguimiento Post-Adopción. Prioridad: Media. |
| Entradas | ID del Contrato de Adopción, Fecha de contacto, Notas de estado, Tipo (Llamada, Visita física). |
| Proceso | Asociar la nota al contrato existente; agendar la próxima fecha de seguimiento según el protocolo del centro. |
| Salidas | Registro de seguimiento guardado en el expediente conjunto. |
| Reglas / Condiciones | Solo aplicable a animales cuyo estado final sea "Adoptado". |

**Módulo 4: Gestión de Visitas y Agenda**

| RF-09 | Agenda de Entrevistas de Evaluación (Pre-Adopción) |
| :---- | :---- |
| Descripción | Programa las entrevistas formales (virtuales o presenciales) entre el evaluador del centro y el candidato a adoptante, previas a conocer al animal. Prioridad: Alta. |
| Entradas | ID del Adoptante (en estado Pendiente), ID del Evaluador (Staff), Fecha, Hora, Modalidad (Presencial/Videollamada), Enlace de reunión (si aplica). |
| Proceso | Guardar la cita en la tabla de Entrevistas; validar que el Evaluador no tenga otra entrevista solapada en ese horario. |
| Salidas | Entrevista agendada visible en el módulo de calendario del staff. |
| Reglas / Condiciones | Solo se puede agendar esta entrevista si el perfil del adoptante tiene todos los datos obligatorios del formulario de adopción completos. |

**Módulo 5: Administración, Reportes y Analítica**

| RF-10 | Gestión de Sedes (Multi-Centro) |
| :---- | :---- |
| Descripción | Administra las ubicaciones físicas de la organización. Incluye: Gestión de Sedes. Prioridad: Alta. |
| Entradas | Nombre de Sede, Dirección, Capacidad máxima, Administrador a cargo. |
| Proceso | Generar estructura jerárquica para que animales e inventarios se subordinen a esta Sede. |
| Salidas | Nueva Sede disponible en todos los menús desplegables del sistema. |
| Reglas / Condiciones | Una sede con animales asignados no puede ser borrada del sistema, solo "Desactivada" (Soft delete). |

| RF-11 | Dashboards de Población y Efectividad |
| :---- | :---- |
| Descripción | Genera estadísticas de ocupación y éxito en las adopciones. (Incluye: Reporte de Población Animal, Reporte de Efectividad de Adopciones). Prioridad: Alta. |
| Entradas | Rango de fechas, Filtros (Sede, Especie, Estado). |
| Proceso | Consultar tablas de Animales y Contratos; agrupar datos; calcular porcentajes de adopción vs ingresos. |
| Salidas | Gráficos visuales (pastel, barras) y tarjetas de métricas en pantalla. |
| Reglas / Condiciones | Los datos visualizados deben ser calculados en tiempo real al momento de cargar el Dashboard. |

## **3.2. Requisitos No Funcionales**

Estos requisitos detallan las cualidades o restricciones del sistema.

| ID | Categoría | Requisito No Funcional | Verificación |
| :---- | :---- | :---- | :---- |
| RNF-001 | Rendimiento | El sistema debe responder a una solicitud estándar de búsqueda en menos de 2 segundos. | Pruebas de Carga  |
| RNF-002 | Seguridad | El sistema debe garantizar que todas las librerías y componentes de terceros utilizados esten libres de vulnerabilidades de seguridad conocidas. | Auditoría de Código |
| RNF-003 | Usabilidad | El sistema debe contar con buenas prácticas de diseño de UI/UX. | Revisión de Interfaz |

# **4\. Roles**

A continuación, se encuentran descritos los roles específicos que cada miembro del equipo tiene dentro del proyecto.

| Miembro del Equipo | Rol |
| :---- | :---- |
| Anchapaxi Ariel | Analista de Requisitos y Calidad |
| Brazales Camilo | Diseñador de Procesos y UX |
| Cadena Juan | Especialista en Pruebas |
| Cerezo José | Arquitecto de Software y Líder Técnico |
| Solórzano Camily | Gestor de Seguridad y Riesgos |

