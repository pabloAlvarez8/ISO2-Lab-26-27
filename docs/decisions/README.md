# Registro de Decisiones de Arquitectura y Diseño

Este directorio almacena las decisiones técnicas relevantes adoptadas por el equipo a lo largo del desarrollo del proyecto.

## Estructura de Decisiones

Las decisiones se documentan indicando:
1. **Contexto:** Problema o necesidad técnica identificada.
2. **Decisión adoptada:** Solución elegida por el equipo.
3. **Alternativas descartadas:** Qué otras opciones se evaluaron y por qué no se eligieron.
4. **Consecuencias:** Impacto técnico, dependencias o limitaciones asumidas.

---

## Decisiones Iniciales

- **ADR-001 - Stack base con Maven Wrapper y Spring Boot:**
  - *Contexto:* Se requiere garantizar la reproducibilidad de la construcción del proyecto en los diferentes puestos de trabajo del equipo.
  - *Decisión:* Usar Maven Wrapper (`mvnw` / `mvnw.cmd`) con Java 21 y Spring Boot, fijando versiones comunes en `pom.xml`.
  - *Consecuencias:* Ningún integrante depende de la instalación manual o versión particular de Maven local.

- **ADR-002 - Adopción de arquitectura por capas:**
  - *Contexto:* Evitar controladores todopoderosos y acoplamiento temprano.
  - *Decisión:* Separación estricta en capas `domain`, `repository`, `service` y `web`.

- **ADR-003 - Prevención de solapamiento de fechas en reservas:**
  - *Contexto:* Es necesario garantizar que ningún alojamiento sufra colisiones de fechas entre reservas activas.
  - *Decisión:* Emplear una consulta JPQL con la condición canónica de intervalos `(b.checkIn < :checkOut AND b.checkOut > :checkIn)` filtrando por reservas en estado `CONFIRMED`. Esto permite que el check-in de una reserva coincida con el check-out de otra sin falso positivo.
  - *Consecuencias:* Detección atómica y óptima a nivel de base de datos H2 antes de crear el registro, evitando sobreventa de alojamientos.

