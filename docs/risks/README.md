# Registro de Riesgos y Supuestos

Este directorio documenta los riesgos técnicos, supuestos iniciales y dependencias externas identificadas por el equipo.

## Gestión de Riesgos

- **R-01 - Desviación del alcance en historias de usuario:**
  - *Mitigación:* Definición previa de criterios de aceptación y slices verticales mínimos antes de codificar.
- **R-02 - Pérdida de trazabilidad o regresiones en `main`:**
  - *Mitigación:* Política estricta de no push directo a `main`, revisión por pares en PR y ejecución obligatoria de `mvnw verify`.
