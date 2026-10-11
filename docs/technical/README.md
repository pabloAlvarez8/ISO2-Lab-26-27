# Base Técnica y Configuración

Este documento detalla la base técnica del proyecto, las herramientas de construcción y la justificación de las dependencias utilizadas.

## Entorno de Construcción y Ejecución

- **Lenguaje:** Java 21
- **Herramienta de compilación:** Maven (a través de Maven Wrapper `mvnw` / `mvnw.cmd`)
- **Framework principal:** Spring Boot 4.1.1 (o versión base configurada en `pom.xml`)

### Comandos de Construcción y Verificación

Para garantizar que el proyecto es reproducible y verificable en cualquier entorno local sin depender de configuraciones del IDE, se emplea el Maven Wrapper:

- **Limpiar artefactos generados:**
  ```bash
  # Windows PowerShell
  .\mvnw.cmd clean
  # Linux / macOS
  ./mvnw clean
  ```

- **Compilar y ejecutar pruebas:**
  ```bash
  # Windows PowerShell
  .\mvnw.cmd test
  # Linux / macOS
  ./mvnw test
  ```

- **Construir el paquete ejecutable:**
  ```bash
  # Windows PowerShell
  .\mvnw.cmd package
  # Linux / macOS
  ./mvnw package
  ```

- **Verificación completa (ciclo estándar):**
  ```bash
  # Windows PowerShell
  .\mvnw.cmd verify
  # Linux / macOS
  ./mvnw verify
  ```

- **Arrancar la aplicación localmente:**
  ```bash
  # Windows PowerShell
  .\mvnw.cmd spring-boot:run
  # Linux / macOS
  ./mvnw spring-boot:run
  ```
  La aplicación responde en: `http://localhost:8080/`

---

## Justificación de Dependencias Base

Cada dependencia incluida en `pom.xml` responde a una necesidad del proyecto para evitar la acumulación de dependencias no utilizadas (deuda técnica):

1. **`spring-boot-starter-webmvc`**:
   - *Propósito:* Proporciona la infraestructura básica para el desarrollo web mediante el patrón Modelo-Vista-Controlador (Spring MVC). Maneja el ciclo de vida de peticiones HTTP, mapeo de rutas (`@GetMapping`, `@PostMapping`), controladores y servidor embebido Tomcat.
2. **`spring-boot-starter-thymeleaf`**:
   - *Propósito:* Motor de plantillas HTML del lado del servidor. Permite renderizar vistas de usuario con datos del modelo de manera declarativa y estructurada.
3. **`spring-boot-starter-webmvc-test`**:
   - *Propósito:* Soporte de pruebas unitarias y de integración para controladores y peticiones web de Spring MVC.
4. **`spring-boot-starter-thymeleaf-test`**:
   - *Propósito:* Utilidades para verificar y probar el renderizado de vistas Thymeleaf.
