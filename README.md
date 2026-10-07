# ISO2-Lab-26-27
# Plataforma de alquiler de viviendas 

Proyecto desarrollado para el laboratorio de Ingeniería del Software II (curso 2026-2027)

## Equipo

Empresa: por definir 

Integrantes:

- Pablo Álvarez Peinado
- Daira Lucia Garcia Mendez
- Adrián Rafael de la cruz dela Rosa
- Alberto López Alcázar
- Daniel Fernandez Palacios

Portavoz:

- por definir

## Objetivo

Desarrollar una plataforma para la gestión de alquiler de viviendas siguiendo un proceso iterativo e incremental de ingeniería del software.

## Estado

Sprint 0 - Preparación y planificación

## Construcción y Ejecución

### Requisitos
- Java 21 (o JDK compatible configurado)
- Maven Wrapper incluido en el proyecto (`mvnw` / `mvnw.cmd`)

### Comandos principales

```bash
# Limpiar artefactos generados
.\mvnw.cmd clean

# Compilar y ejecutar pruebas unitarias
.\mvnw.cmd test

# Empaquetar la aplicación
.\mvnw.cmd package

# Verificación completa del proyecto
.\mvnw.cmd verify

# Iniciar la aplicación localmente
.\mvnw.cmd spring-boot:run
```

- **URL local de acceso:** [http://localhost:8080/](http://localhost:8080/)
- **Cómo detener la aplicación:** `Ctrl + C` en la terminal de ejecución.

## Documentación del Proyecto

La documentación viva del proyecto se mantiene estructurada en:

- `docs/technical/`: Base técnica, comandos de ejecución y justificación de dependencias.
- `docs/decisions/`: Registro de decisiones de arquitectura (ADRs) y diseño.
- `docs/diagrams/`: Modelos de casos de uso y diagramas de dominio.
- `docs/risks/`: Riesgos identificados y mitigaciones.
- `docs/ai/`: Fichas de evidencias de uso de IA agéntica (Hitos 1, 2 y 3).
- `AI_USAGE.md`: Registro global de intervenciones de IA.
- `AGENTS.md`: Instrucciones y restricciones para agentes de desarrollo.

## Plan de Gestión de Configuración (Contrato Git)

1. **Rama principal protegida:** No se realiza push directo a `main`.
2. **Trazabilidad:** Todo cambio relevante tiene una tarea/issue asociada en GitHub.
3. **Flujo de integración:** Todo cambio pasa por una rama de trabajo propia (`<tipo>/<issue>-<descripcion>`) y se integra en `main` mediante Pull Request (PR).
4. **Revisión por pares:** Toda PR relevante debe ser revisada y aprobada por otro miembro del equipo antes del merge.
5. **Criterio de integración (DoD):** Solo se realiza merge si `mvnw verify` pasa con éxito y se cumplen los criterios de aceptación y documentación.
