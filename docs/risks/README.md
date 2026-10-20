# Registro de Riesgos y Supuestos

Este directorio documenta los riesgos técnicos, supuestos iniciales y dependencias externas identificadas por el equipo.

## Gestión de Riesgos

- **R-01 - Desviación del alcance en historias de usuario:**
  - *Mitigación:* Definición previa de criterios de aceptación y slices verticales mínimos antes de codificar.
- **R-02 - Pérdida de trazabilidad o regresiones en `main`:**
  - *Mitigación:* Política estricta de no push directo a `main`, revisión por pares en PR y ejecución obligatoria de `mvnw verify`.
- **R-03 - Inconsistencia de atributos JPA en `Property` entre ramas paralelas (persistencia vs. buscador):**
  - *Contexto:* La rama de persistencia (#23/#31) modeló atributos según el modelo UML (`address`, `destination`, `type`, `directBooking`), mientras la rama de búsqueda (#35) introdujo atributos enriquecidos (`title`, `city`, `propertyType`, `available`, `rating`, `reviewCount`).
  - *Mitigación:* Fusión integral de atributos en `Property.java`, inicialización de valores por defecto seguros y sincronización bidireccional defensiva en constructores y setters (`address` ⇄ `title`, `destination` ⇄ `city`, `type` ⇄ `propertyType`).
- **R-04 - Incompatibilidad de nombres de atributos en el `Model` para plantillas Thymeleaf:**
  - *Contexto:* La vista `home.html` itera sobre `${inmuebles}`, mientras `search.html` itera sobre `${properties}`, pudiendo ocasionar errores de renderizado o listados vacíos.
  - *Mitigación:* Exponer simultáneamente `inmuebles` y `properties` en el `Model` desde `HomeController`, garantizando interoperabilidad transparente entre todas las vistas.
- **R-05 - Fallo en consultas JPQL multicriterio ante alias de campos o propiedades nulas:**
  - *Contexto:* La consulta `searchProperties` en `PropertyRepository` filtraba exclusivamente por `p.city` y `p.title`; alojamientos guardados con `destination` o `address` quedarían excluidos de las búsquedas.
  - *Mitigación:* Uso de `COALESCE` en la consulta JPQL (`COALESCE(p.city, p.destination, '')`, `COALESCE(p.title, p.address, '')`, `COALESCE(p.propertyType, p.type, '')`) y filtro permisivo para `available` nulo o verdadero.
- **R-06 - Desincronización de orden de inicialización H2 en data.sql:**
  - *Contexto:* Si Spring Boot ejecuta los scripts de datos antes de que Hibernate genere las tablas en memoria, se producen errores `Table not found`.
  - *Mitigación:* Añadir `spring.jpa.defer-datasource-initialization=true` en `application.properties`.
- **R-07 - Regresión en tests automatizados por presencia de datos semilla:**
  - *Contexto:* Pruebas de integración como `HomeControllerTest` esperan que el catálogo comience vacío ("No hay alojamientos disponibles"). Si se ejecuta `data.sql` en pruebas, fallan las aserciones.
  - *Mitigación:* Crear `src/test/resources/application.properties` con `spring.sql.init.mode=never` para evitar la carga de semillas durante la ejecución de los tests.
- **R-08 - Desfase métrico entre reseñas y filtros multicriterio de propiedades:**
  - *Contexto:* Los filtros de búsqueda por estrellas (`minRating`) y número de opiniones (`minReviews`) leen atributos desnormalizados en `Property`. Si una nueva reseña no actualiza dichos valores de forma atómica, los filtros arrojarán resultados inconsistentes.
  - *Mitigación:* `ReviewService` actualiza atómicamente la media aritmética y el recuento total en `Property` dentro de la transacción de creación de la reseña.
- **R-09 - Desalineación de formato numérico en selector interactivo de estrellas cliente-servidor:**
  - *Contexto:* Al sustituir un control estándar `<select>` por estrellas interactivas basadas en mitades SVG, un fallo en la captura de eventos o formato localizado (coma vs. punto decimal) provocaría rechazos HTTP 400 o fallos de validación en `ReviewService`.
  - *Mitigación:* Centralización del valor en un campo `<input type="hidden" name="rating">` con formato canónico en punto decimal (`0.5` a `5.0`), soporte dual de interacción (*hover* y *click/touch*) y preselección de valor por defecto (`5.0`).
- **R-10 - Renderizado inconsistente de tarjetas por imágenes no disponibles o nulas:**
  - *Contexto:* Al incluir cabeceras de imagen en las tarjetas del catálogo principal (`home.html`), alojamientos con `imageUrl` nulo o vacío provocarían iconos de imagen rota o descuadres en el layout visual.
  - *Mitigación:* Evaluación condicional en Thymeleaf con imagen por defecto de alta resolución de Unsplash y contención CSS mediante `overflow-hidden` y `object-cover`.

