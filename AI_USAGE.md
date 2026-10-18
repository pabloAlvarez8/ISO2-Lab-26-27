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
