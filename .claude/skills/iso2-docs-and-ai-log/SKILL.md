---
name: iso2-docs-and-ai-log
version: 1.0.0
description: Use when asked to update project documentation, ADRs, diagrams, or record AI interventions in AI_USAGE.md and docs/.
---
# ISO2 Documentation & AI Usage Logging Workflow
1. **AI Usage Log (`AI_USAGE.md` & `docs/ai/`)**:
   - Record concisely: Date/Milestone, Task/Issue ID, AI tool used, summary of AI proposal, human review/modifications made, and verification command run (`./mvnw verify`).
2. **Architecture Decisions (`docs/decisions/README.md`)**:
   - When a technical choice is made (e.g., DB config, styling with Tailwind, validation strategy), append an ADR entry with: Title, Status, Context, Decision, Alternatives Considered, and Consequences.
3. **Sync Check**:
   - If `domain` entities changed, remind the user to update class/domain diagrams in `docs/diagrams/README.md`.
