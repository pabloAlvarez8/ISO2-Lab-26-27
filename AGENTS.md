# Project Instructions — AlohaRent (es.uclm.rental_platform)

## Communication & Output Rules
- Always reply in Spanish.
- Be strictly concise: give only the minimum context needed to understand the solution.
- No greetings, conversational filler, or closing summaries.
- Do not explain step-by-step unless the user explicitly asks for details.

## Tech Stack & Commands
- Stack: Java 21, Spring Boot 4.1.1, Spring MVC, Thymeleaf, Spring Data JPA, JUnit 5.
- Always check `pom.xml` and `application.properties` before proposing config fixes.
- Always use Maven Wrapper (never global `mvn`):
  - Clean/Compile: `./mvnw clean test-compile`
  - Test all: `./mvnw test` | Single test: `./mvnw test -Dtest=Class#method`
  - Verify (DoD required): `./mvnw verify`
  - Run app: `./mvnw spring-boot:run` (http://localhost:8080/)
- GitHub CLI (Read-only):
  - Inspect issue: `gh issue view <number>`

## Architecture Rules (ADR-002)
- Base package: `es.uclm.rental_platform`
- Strict layered packages: `domain`, `repository`, `service`, `web` (Controller -> Service -> Repository -> Persistence).
- Zero business logic in `@Controller` (`web` layer).
- Dependency Injection: Constructor injection only (never `@Autowired` on fields).
- Views: Server-side rendered HTML in `src/main/resources/templates/` using Thymeleaf.
- JPA Portability (DB-agnostic):
  - Default `@ManyToOne` / `@OneToMany` to `FetchType.LAZY`.
  - Put `@Transactional` on `@Service`, never on `@Controller`.
  - Escape SQL reserved words in `@Table` (`users`, `orders`, `bookings`).

## Mandatory Development Process (ISO2 Lab)
Before implementing a task:
1. Read the related issue and its acceptance criteria.
2. Inspect the relevant repository files.
3. Propose an implementation plan.
4. Identify affected components and possible risks.
5. Do not make changes until the plan has been reviewed.

After implementation:
1. Build and verify the project (`./mvnw verify`).
2. Execute the relevant tests.
3. Explain the changes performed concisely.
4. Report any unresolved problem or assumption for `AI_USAGE.md`.

## Restrictions
- Do not invent requirements or silently change architecture.
- Do NOT write unit/slice test implementations unless explicitly requested by the user (only create empty test class skeletons if needed).
- Do not remove existing tests to make the build pass.
- Do not integrate or approve changes.
- NEVER run `git commit`, `git push`, `git merge`, or create/merge/approve Pull Requests (`gh pr`). All Git write operations are strictly human-only.
