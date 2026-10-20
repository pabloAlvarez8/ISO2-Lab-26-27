# AI-01 — Hito 1 · Implementación de una issue funcional

> **Ingeniería del Software II · Laboratorio 2026-2027**
>
> Esta ficha documenta la ejecución del **Hito 1 de IA agéntica**.
>
> El objetivo es dejar evidencia de cómo el equipo utiliza un agente para apoyar la implementación de una issue funcional, qué revisa o modifica antes de aceptar la propuesta y cómo verifica el resultado.

---

## 1. Identificación

- **Equipo / empresa:** Software Company Rental Platform
- **Integrantes:** (Tus datos / nombres del equipo)
- **Repositorio:** software-company-rental-platform
- **Issue relacionada:** #23
- **Historia de usuario:** Como propietario, quiero registrar un inmueble para poder ofrecerlo posteriormente en alquiler.
- **Rama de trabajo:** feat/23-property-persistence
- **Responsable principal del Hito 1:** (Tu nombre)
- **Participantes y contribución de cada uno:** Desarrollo del flujo vertical persistente, configuración de H2 y diseño de vistas en Thymeleaf.

---

## 2. Alcance de la issue

### Objetivo

Implementar el primer flujo vertical persistente en Spring Boot utilizando JPA y base de datos H2. Permite registrar un inmueble desde un formulario web, validarlo, almacenarlo en la base de datos y consultarlo dinámicamente en el catálogo de la página principal.

### Criterios de aceptación relevantes

- [x] Configuración de dependencias JPA y H2 en el proyecto.
- [x] Creación de la entidad `Property` adaptada al modelo de dominio (dirección, precio, tipo, destino, capacidad y reserva inmediata).
- [x] Implementación de repositorios, servicios con lógica de negocio y controladores web.
- [x] Verificación visual de los datos guardados mediante la consola H2 y renderización en la web.

### Fuera de alcance

- Autenticación completa de usuarios y vinculación obligatoria de propietarios reales en esta iteración.
- Funcionalidades avanzadas de pasarela de pago o reservas completas.

---

## 3. Plan humano previo

> Completar antes de pedir al agente que proponga su solución.

### Plan inicial del equipo

1. Añadir/verificar dependencias de Spring Data JPA y H2 en el `pom.xml`.
2. Configurar `application.properties` con la URL de H2 en memoria y las opciones de Hibernate (`ddl-auto=update`).
3. Crear la entidad JPA `Property` dentro del paquete `domain`.
4. Configurar la interfaz `PropertyRepository` y el servicio `PropertyService` con validaciones de negocio.
5. Desarrollar el `PropertyController` y conectar la vista Thymeleaf (`properties.html` y `home.html`).

### Archivos o capas que se prevé modificar

- `pom.xml` / `application.properties`
- Paquete `domain/Property.java`
- Paquete `repository/PropertyRepository.java`
- Paquete `service/PropertyService.java`
- Paquete `web/PropertyController.java` y `HomeController.java`

### Dependencias, supuestos o dudas

- Uso temporal de H2 en memoria para agilizar el desarrollo del laboratorio.
- Validación de duplicados por dirección en el servicio.

### Cómo se comprobará que funciona

- Ejecución de pruebas de compilación (`mvnw verify`).
- Registro manual a través de la ruta `/inmuebles/alta`.
- Comprobación de filas en la consola H2 y visualización de tarjetas en la página principal (`/`).

---

## 4. Contexto y propuesta del agente

### Contexto proporcionado

- [x] Repositorio abierto
- [x] Rama correcta
- [x] `AGENTS.md` y skills aplicables
- [x] Issue y criterios de aceptación
- [x] Código existente relevante
- [x] Estructura Maven / Spring Boot
- [x] Otro: Documentación del proyecto (UML y enunciado)

**Herramienta / agente:** Gemini / ChatGPT (según se use)
**Entorno:** VS Code
**Modelo, si procede:** GPT-5.6 / Gemini Pro

### Prompt principal

```text
Analiza el repositorio y ayúdame a estructurar un flujo vertical persistente para la issue #23 (registrar y listar inmuebles con JPA y H2 adaptado al diagrama UML).

### Plan propuesto por el agente

1.  Configurar JPA y H2 en propiedades.
2.  Mapear la entidad Property con sus respectivos atributos de dominio.
3.  Conectar repositorio, servicio con validaciones y controlador web.

**Archivos o componentes propuestos:**

- Property.java
- PropertyRepository.java
- PropertyService.java
- PropertyController.java

---

## 5. Revisión humana del plan

| Propuesta del agente | Decisión | Justificación |

| Crear subsistema de Owner completo | Rechazada | Aumentaba innecesariamente el alcance para la primera iteración de persistencia. |
| Usar API REST pura | Modificada | El proyecto requiere vistas web con Thymeleaf alineadas con el resto de la aplicación. |
| Estructura básica de capas JPA | Aceptada | Encaja perfectamente con la arquitectura requerida en el laboratorio. |

### Cambios solicitados antes de implementar

- Ajustar los atributos de la entidad a los requeridos por el enunciado del proyecto (address, pricePerNight, type, destination, capacity, directBooking).
- Implementar validaciones para evitar duplicados por dirección.

### Plan final aprobado

1.  Configuración de persistencia mínima.
2.  Creación de entidad adaptada al modelo UML de la asignatura.
3.  Desarrollo de capas de Servicio y Controlador con control de excepciones visuales.

---

## 6. Implementación y revisión del código

### Cambios principales

| Archivo / componente | Cambio realizado | Motivo |

| Property.java | Creación de entidad JPA con anotaciones y atributos de dominio. | Definir la estructura de la tabla en H2. |
| PropertyRepository.java | Interfaz extendiendo de JpaRepository | Proveer métodos de acceso a datos. |
| PropertyService.java | Lógica de negocio y validación de duplicados. | Evitar datos erróneos o repetidos en el sistema. |
| PropertyController.java | Gestión de peticiones GET y POST para el alta. | Coordinar la interfaz web con el servicio. |
| home.html | Modificación del catálogo estático para integrar un bucle dinámico con Thymeleaf (th:each). | Permitir visualizar en tiempo real los inmuebles almacenados en la base de datos tras cada registro. |

### Revisión humana

- [x] Los cambios pertenecen a la issue.
- [x] No se ha ampliado innecesariamente el alcance.
- [x] La separación por capas se mantiene.
- [x] La lógica de negocio no se concentra en el controlador.
- [x] Las entidades y relaciones JPA se pueden justificar, si procede.
- [x] Las validaciones y errores son controlados.
- [x] No se han añadido archivos temporales o ajenos.
- [x] El equipo comprende los cambios realizados.

### Problemas o carencias detectados

| Problema | Corrección / decisión |

| Error 500 al intentar duplicar inmuebles | Implementación de bloque try-catch en el controlador para mostrar alertas visuales de error. |

---

## 7. Verificación

### Build

```powershell
.\mvnw.cmd verify
```

- [x] `BUILD SUCCESS`
- [ ] Fallo

**Observaciones:**  



### Ejecución de la funcionalidad

```powershell
.\mvnw.cmd spring-boot:run
```

- [x] La aplicación arranca correctamente.
- [x] La funcionalidad puede ejecutarse desde la interfaz o endpoint previsto.

### Comprobación funcional

**Caso válido**

- **Entrada / acción: Rellenar formulario en /inmuebles/alta con datos correctos.**  
- **Resultado esperado: Guardado en base de datos y redirección con éxito.**  
- **Resultado obtenido: Inmueble guardado y visible dinámicamente en el catálogo de la home.**  

**Caso inválido o error controlado**

- **Entrada / acción: Registrar un inmueble con una dirección ya existente.**  
- **Resultado esperado: Mensaje de error controlado en pantalla.**  
- **Resultado obtenido: Alerta visual de error desplegada correctamente sin excepciones 500.**  

### Persistencia, si procede

- **Qué se comprobó: Existencia de registros mediante H2 Console (SELECT * FROM PROPERTIES;)**  
- **Resultado: Los datos sobreviven y se recuperan correctamente tras las consultas.**  

### Revisión final del cambio

```powershell
git status
git diff
```

- [x] Solo aparecen cambios relacionados con la issue.
- [x] El equipo ha revisado y comprende el diff.
- [x] No hay archivos generados, temporales o ajenos.

---

## 8. Evidencias

| Evidencia | Captura / enlace |
|---|---|
| Plan propuesto por el agente | (Registrado en esta ficha) |
| Revisión / cambio humano relevante | Adaptación de atributos al UML de la asignatura |
| Funcionalidad ejecutada | docs/img/form.jpg |
| Verificación / persistencia, si procede | docs/img/h2console.jpg |
| Pull Request | https://github.com/pabloAlvarez8/ISO2-Lab-26-27/pull/30 |

---

## 9. Pull Request y cierre

- **PR: Enlace a la PR creada en develop**  
- **Issue vinculada: #23**  
- **Revisor/a: Adrián**  
- **Estado: En revisión** Abierta / En revisión / Aprobada / Integrada  

### Revisión de la PR

- [x] Alcance correcto.
- [x] Diseño y separación por capas revisados.
- [x] Build correcto.
- [x] Persistencia y validaciones revisadas, si procede.
- [x] Documentación actualizada.
- [x] Uso de IA declarado.
- [x] Deuda técnica, limitaciones o cuestiones pendientes identificadas.

### Observaciones del revisor

- 
- 

---

## 10. Conclusión y defensa

### Conclusión

- **Qué aportó el agente: Propuesta inicial de estructura de clases y boilerplate para la persistencia.**  
- **Qué tuvo que revisar o corregir el equipo: Adaptación de los campos a las restricciones del modelo UML y filtrado de sobreingeniería (como evitar la creación de entidades de usuarios adicionales en esta fase).**  
- **Qué propuesta no debía aceptarse directamente: La creación de un subsistema de propietarios complejo que rompía el acotamiento de la issue.**  
- **Qué decisión humana fue más importante: Definir el modelo de atributos exacto del inmueble (capacity, pricePerNight, directBooking) según el dominio.**  
- **Cómo se verificó: Mediante mvnw verify, pruebas visuales en H2 Console y comprobación de rutas web.**  

### Estado final

- [x] Issue implementada.
- [x] Build correcto.
- [x] Funcionalidad verificada.
- [x] Persistencia comprobada, si procede.
- [x] Diff revisado.
- [x] Evidencias completadas.
- [x] PR revisable.
- [x] Documentación actualizada cuando corresponde.

### Resumen breve para defensa

**Issue: #23 - implementación de la BD - Publicar un inmueble**  
**Qué hizo el agente: Generar la estructura base de clases para JPA y H2.**  
**Qué modificó o rechazó el equipo: Se descartó el módulo complejo de propietarios y se adaptaron los campos de los inmuebles al UML del proyecto.**  
**Cómo se verificó: Con mvnw verify, pruebas de inserción web y consulta en consola H2.**  
**Conclusión: Se logró un incremento funcional sólido, persistente y debidamente validado.**  

---

### Regla final

> **La IA puede proponer e implementar cambios, pero no puede aprobar su propio trabajo.**
>
> El equipo es responsable de comprender, revisar, verificar y justificar todo lo que se integra en el proyecto.