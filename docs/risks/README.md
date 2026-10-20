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
