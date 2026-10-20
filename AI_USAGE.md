# AI Usage Log (Registro de uso de IA)

This document records all relevant Artificial Intelligence (AI) interventions performed during the development of the **AlohaRent** project, ensuring full traceability, human oversight, and engineering rigor.

## Core Principles

- **Human Decision Authority:** AI may propose solutions, but the engineering team makes the final decision.
- **Full Comprehension & Review:** Every code or configuration change must be thoroughly understood and reviewed by a team member before adoption.
- **Automated & Tool Verification:** All AI proposals must be verified using the project's official toolchain (`./mvnw test`, `./mvnw verify`, static analysis).
- **Mandatory Human Gatekeeping:** No AI-generated code or artifact shall be integrated into `develop` or `main` without prior human review via Pull Request.
- **Strict Version Control:** AI agents are strictly forbidden from executing Git write operations (`commit`, `push`, `merge`, or `pr`).

## Logging Template (Standard Entry Format)

For each relevant AI-assisted task, record:
- **Date & Milestone:** `YYYY-MM-DD` | Milestone (`Hito 1`, `Hito 2`, `Hito 3`)
- **Task / Issue:** Related GitHub Issue or branch name
- **AI Agent / Model Used:** Tool and model (e.g., Antigravity, Gemini CLI, Claude)
- **AI Proposal Summary:** What the agent generated or analyzed
- **Human Review & Adjustments:** What the team modified, rejected, or refined
- **Verification Executed:** Commands run to validate the output

---

## Milestone 1 - Functional Development (Hito 1 - Desarrollo funcional)

### Entry 1.1 — AI Agentic Architecture & Context Setup (v1.0.0)
- **Date:** 2026-10-19
- **Branch / Task:** `chore/ai-agents-setup` — Configure repository context files (`AGENTS.md`, `GEMINI.md`, `CLAUDE.md`), modular skills (`SKILL.md`), and read-only sub-agents.
- **AI Proposal Summary:** Initial boilerplate for context rules, vertical-slice skills, and JPA/domain reviewers.
- **Human Review & Adjustments:**
  - Removed model-specific role restrictions from root files so any active model operates full-stack without artificial limitations.
  - Made JPA rules database-agnostic (decoupled from H2-specific constraints) to support future DB engine migrations.
  - Configured testing policy so agents do not write unrequested tests during feature implementation, while strictly auditing tests once written.
  - Added strict prohibition against AI-executed Git commits, pushes, or Pull Requests.
- **Verification Executed:** Manual inspection of `git diff`, directory structure validation, and `./mvnw test-compile`.

### Entry 1.2 — Login & Registration Integration with H2 Persistence & AlohaRent UI (feat/17-login-registro)
- **Date:** 2026-10-19
- **Branch / Task:** `feat/17-login-registro` — Integration of stash UI (Airbnb-style AlohaRent views, global persistent search bar, login and registration templates) with backend persistence in H2, BCrypt password hashing, and test suite.
- **AI Proposal Summary:** Resolved merge conflict in `home.html`, updated `fragments/header.html` with dynamic session management, integrated error/success feedback and role selection into `login.html` and `register.html`, added transactionality and validation to `UserService`, and created unit/slice tests for service and web controllers.
- **Human Review & Adjustments:** Implementation plan reviewed and approved before applying changes; Git write commands restricted to human-only.
- **Verification Executed:** `.\mvnw.cmd test` and `.\mvnw.cmd verify` passing 100% (17/17 tests passing).

### Entry 1.3 — Auto-login upon Registration, Dedicated Dashboard & Profile View (feat/17-login-registro)
- **Date:** 2026-10-19
- **Branch / Task:** `feat/17-login-registro` — Implement auto-login on registration, dedicated user dashboard/profile view with guest protection, and domain exceptions.
- **AI Proposal Summary:** Created `dashboard.html` with dual view states (restricted view for anonymous users showing only Login/Register/Home options, and full profile view for authenticated users with Home/Profile/Logout actions), enabled auto-login in `AuthController.processRegister`, introduced `UserAlreadyExistsException` and `InvalidCredentialsException`, and expanded test suite to 21 tests.
- **Human Review & Adjustments:** Plan approved before changes; user specified exact options for anonymous visitors to dashboard.
- **Verification Executed:** `.\mvnw.cmd test` and `.\mvnw.cmd verify` passing 100% (21/21 tests passing).

### Entry 1.4 — View Separation: Streamlined Dashboard & Dedicated Profile (feat/17-login-registro)
- **Date:** 2026-10-19
- **Branch / Task:** `feat/17-login-registro` — Separate `/dashboard` (focused actions) and `/profile` (account details without redundant self-links).
- **AI Proposal Summary:** Created `profile.html` rendering account details from database without redundant "Ver perfil" button; simplified `dashboard.html` to exclusively offer the 3 requested actions (Ver perfil, Volver a inicio, Cerrar sesión); updated `AuthController.showProfile` to render `profile.html` with guest protection, and updated web slice tests.
- **Human Review & Adjustments:** Plan approved before execution.
- **Verification Executed:** `.\mvnw.cmd test` and `.\mvnw.cmd verify` passing 100% (21/21 tests passing).
### Entry 1.5 — Post-Registration Landing Dashboard Isolation & Profile Actions Refinement (feat/17-login-registro)
- **Date:** 2026-10-19
- **Branch / Task:** `feat/17-login-registro` — Isolate post-registration dashboard landing page, ensuring the dashboard appears only after registration, and removing panel navigation from the profile page.
- **AI Proposal Summary:** Removed "Volver al panel" button from `profile.html` so dashboard is only visited immediately following registration; updated `fragments/header.html` to link directly to `/profile`; and updated assertions in `WebControllersTest.java` to verify profile contains only home/logout actions.
- **Human Review & Adjustments:** Plan approved before execution.
- **Verification Executed:** `./mvnw verify` passing 100% (21/21 tests passing).

### Entry 1.6 — Modularization of Reusable Thymeleaf Fragments & Welcome Banner Refinement (feat/17-login-registro)
- **Date:** 2026-10-19
- **Branch / Task:** `feat/17-login-registro` — Modularize reusable UI fragments into dedicated folders (`fragments/layout/`, `fragments/components/`) and refine session welcome banner.
- **AI Proposal Summary:** Extracted monolithic `fragments/header.html` into `fragments/layout/head.html`, `fragments/layout/header.html`, and `fragments/layout/footer.html`; created `fragments/components/banner.html` and `alerts.html`; migrated all view templates (`home.html`, `login.html`, `register.html`, `dashboard.html`, `profile.html`); removed logout action from the top welcome banner and added an interactive dismiss ("X") button.
- **Human Review & Adjustments:** Implementation plan submitted and approved; user confirmed requirement to remove logout option from banner and add dismissal button; backward-compatibility preserved.
- **Verification Executed:** `./mvnw test` and `./mvnw verify` passing 100% (21/21 tests passing).

### Entry 1.7 — Registration Input Validation, Home Empty State & Robust Verification Suite (feat/17-login-registro)
- **Date:** 2026-10-19
- **Branch / Task:** `feat/17-login-registro` — Implement strict input validation for user registration (minimum 8 characters for password and RFC-compliant email regex), replace fake accommodations with empty state in `home.html`, and implement comprehensive boundary and negative test suites.
- **AI Proposal Summary:** Created `InvalidRegistrationDataException`; enhanced `UserService.registerUser` with email format checking (`EMAIL_PATTERN`) and 8-character password length constraint; removed mockup accommodation cards and filter bar from `home.html` in favor of a clean empty state; created parametrized and boundary tests in `UserServiceTest.java` (testing boundary 7 vs 8 chars, email format variants, null/blank inputs) and web slice tests in `WebControllersTest.java` checking persistence guarantees and error views.
- **Human Review & Adjustments:** Plan reviewed and approved; user instructed to create rigorous tests verifying consistent business logic rather than trivial tests.
- **Verification Executed:** `./mvnw test` and `./mvnw verify` passing 100% (32/32 tests passing).
### Entry 1.8 — Test Suite Reorganization by Architectural Layer (feat/17-login-registro)
- **Date:** 2026-10-19
- **Branch / Task:** `feat/17-login-registro` — Reorganize login and registration test suites according to component responsibilities (persistence/repository, business logic/service, HTTP/controllers).
- **AI Proposal Summary:** Created `UserRepositoryTest` (`@DataJpaTest`) in `es.uclm.rental_platform.persistence` to test persistence, queries (`findByEmail`, `existsByEmail`), and unique email database constraint (`DataIntegrityViolationException`); isolated unit tests in `UserServiceTest` for business validation and authentication; verified `WebControllersTest` for HTTP flow, sessions, redirects, and views. Total tests: 38.
- **Human Review & Adjustments:** Implementation plan submitted and approved; no unnecessary security test class added as authentication is handled without custom security filter chains.
- **Verification Executed:** `./mvnw test` and `./mvnw verify` passing 100% (38/38 tests passing).

### Entry 1.9 — Spring Security Integration & Component-Specific Test Suite Modularization (feat/17-login-registro)
- **Date:** 2026-10-19
- **Branch / Task:** `feat/17-login-registro` — Implement Spring Security configuration (`SecurityConfig`), configure active CSRF protection, synchronize `SecurityContext` in `AuthController`, and modularize tests into component-focused suites (`AuthControllerTest`, `HomeControllerTest`, `SecurityConfigTest`, `UserServiceTest`, `UserRepositoryTest`).
- **AI Proposal Summary:** Added `spring-boot-starter-security` and `spring-security-test` to `pom.xml`; created `SecurityConfig.java` configuring public endpoints, protected `/profile`, `LoginUrlAuthenticationEntryPoint`, active CSRF protection (ignoring only H2 console), and `PasswordEncoder` bean; injected `PasswordEncoder` in `UserService`; synchronized `SecurityContext` in `AuthController`; divided monolithic controller tests into `AuthControllerTest` and `HomeControllerTest`; added `SecurityConfigTest` verifying public/protected access rules and CSRF rejection on missing tokens; removed obsolete `WebControllersTest`. Total tests: 44.
- **Human Review & Adjustments:** User requested to mitigate risks logically and securely (enabling active CSRF protection rather than disabling it, using `.with(csrf())` in tests, and integrating Spring Security authentication).
- **Verification Executed:** `./mvnw test` and `./mvnw verify` passing 100% (44/44 tests passing).

### Entry 1.10 — Accommodation Booking Slice with Date Overlap Validation & Full Layer Testing (feat/31-booking-reservation)
- **Date:** 2026-10-20
- **Branch / Task:** `feat/31-booking-reservation` — Implement accommodation booking vertical slice (Issue #31) encompassing JPA domain modeling, date overlap query prevention, business logic invariants, Thymeleaf booking views, and full test suite.
- **AI Proposal Summary:** Proposed and implemented `Booking` entity and `BookingStatus` enum; created `BookingRepository` with JPQL query detecting date overlap intervals (`checkIn < :checkOut AND checkOut > :checkIn`); developed `BookingService` with invariants (capacity constraints, future date checks, price calculation based on total nights); built `BookingController` and Thymeleaf views (`booking/book-property.html` and `booking/my-bookings.html`) with CSRF protection and alert feedback; and implemented 23 new tests across persistence (`BookingRepositoryTest`), service (`BookingServiceTest`), and web controller (`BookingControllerTest`). Total test suite expanded to 67 tests.
- **Human Review & Adjustments:** Human developer reviewed and approved the implementation plan prior to modification; confirmed incremental multi-commit roadmap for traceability; verified edge cases such that matching check-out and check-in days do not trigger false positive overlaps.
- **Verification Executed:** `.\mvnw.cmd test` and `.\mvnw.cmd verify` passing 100% (67/67 tests passing).

### Entry 1.11 — Resolution of Merge Conflicts across Property Domain, Persistence, Service, and Web Controllers (chore/pr-35-conflict-fix)
- **Date:** 2026-10-20
- **Branch / Task:** `chore/pr-35-conflict-fix` — Resolve merge conflicts between branch `origin/develop` (PR #34, Issues #23 and #31) and PR #35 (Search vertical slice and reactive UI filters), unifying the `Property` domain entity, repository query mechanisms, service validation/search logic, and home web controller without dropping any functionality.
- **AI Proposal Summary:** Proposed and implemented unified `Property.java` combining UML domain fields (`address`, `destination`, `type`, `directBooking`) and extended search fields (`title`, `city`, `propertyType`, `available`, `rating`, `reviewCount`) with bidirectional defensive synchronization in constructors and setters; merged `PropertyRepository` providing `existsByAddress`, `findByAvailableTrue`, and multi-criteria JPQL query with `COALESCE` handling; combined `PropertyService` with transactional property registration, defensive field syncing, and multi-parameter search; updated `HomeController` to provide both `"inmuebles"` and `"properties"` to the view model; and documented risks R-03, R-04, and R-05 with technical mitigations in `docs/risks/README.md`.
- **Human Review & Adjustments:** Human developer reviewed and approved the implementation plan prior to modification, requesting explicit documentation and resolution of risks in `docs/risks/README.md`.
- **Verification Executed:** Executed `.\mvnw.cmd clean test-compile`, `.\mvnw.cmd test`, and `.\mvnw.cmd verify` (DoD required) with 100% passing rate (67/67 tests passing, `BUILD SUCCESS`).

