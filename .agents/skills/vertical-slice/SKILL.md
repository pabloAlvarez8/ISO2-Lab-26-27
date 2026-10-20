---
name: vertical-slice
version: 1.0.0
description: Use when implementing a new feature, user story, CRUD, entity, service, controller, or Thymeleaf view in AlohaRent.
---
# Spring Boot Vertical Slice Workflow (AlohaRent)
1. **Plan First**: Read issue acceptance criteria (`gh issue view <id>`), list affected files across `domain`, `repository`, `service`, `web`, and `templates/`, and wait for user approval.
2. **Domain (`es.uclm.rental_platform.domain`)**:
   - Create `@Entity` with `@Id @GeneratedValue(strategy = GenerationType.IDENTITY)`.
   - Escape SQL reserved table names (`users`, `bookings`, `properties`).
   - Default `@ManyToOne` / `@OneToMany` to `FetchType.LAZY`.
3. **Repository (`es.uclm.rental_platform.repository`)**:
   - Extend `JpaRepository<Entity, Long>`.
4. **Service (`es.uclm.rental_platform.service`)**:
   - Use `@Service`, constructor injection, `@Transactional(readOnly = true)` at class level, and `@Transactional` on write methods.
   - Enforce domain invariants (`price > 0`, `checkIn < checkOut`, no overlapping bookings).
5. **Web & UI (`es.uclm.rental_platform.web` & `src/main/resources/templates/`)**:
   - `@Controller` with zero business logic; delegate everything to `@Service`.
   - Build responsive HTML5 Thymeleaf templates (`xmlns:th="http://www.thymeleaf.org"`) styled with **Tailwind CSS** (`<script src="https://cdn.tailwindcss.com"></script>`).
6. **Tests Policy**:
   - Do NOT implement test methods unless explicitly asked. If needed, create only an empty test class skeleton in `src/test/`.
7. **Verify**: Run `./mvnw test-compile` (or `./mvnw verify`) to confirm zero compilation errors.
