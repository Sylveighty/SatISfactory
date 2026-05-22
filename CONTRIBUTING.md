# Contributing to PUP SIS

This document describes the development workflow for the PUP SIS project.
It is written for student contributors familiar with Git basics.

---

## Workflow Overview

This project follows an **Agile Kanban** methodology.

Tasks live on the Kanban board in five states:

Backlog → To Do → In Progress → Testing → Done

Before starting any work, check the board. Pick a task from **To Do**, move it to **In Progress**, and open a feature branch.

---

## Branching Strategy

| Branch | Purpose |
|---|---|
| `main` | Stable, demo-ready code only. Never commit directly. |
| `dev` | Integration branch. Feature branches merge here via PR. |
| `feat/<name>` | New features. Branch from `dev`. |
| `fix/<name>` | Bug fixes. Branch from `dev`. |
| `chore/<name>` | Config, tooling, dependency updates. Branch from `dev`. |

**Creating a feature branch:**

```bash
git checkout dev
git pull origin dev
git checkout -b feat/student-crud
```

**Opening a Pull Request:**

- PR always targets `dev`, not `main`
- Use the PR template (auto-filled when you open a PR on GitHub)
- At least one reviewer should approve before merging
- Merge `dev` → `main` only after a step is complete and tested

---

## Commit Convention

Use the format: `type(scope): short imperative description`

| Type | When to use |
|---|---|
| `feat` | New feature or page |
| `fix` | Bug fix |
| `chore` | Configuration, tooling, non-feature |
| `docs` | Documentation only |
| `refactor` | Code restructure without behaviour change |
| `style` | CSS/formatting changes |
| `test` | Adding or updating tests |

**Scope** is the layer or module affected:

feat(entity): add Course entity with name and code fields
feat(controller): add StudentController with list and add endpoints
fix(security): correct redirect URL for unauthenticated access
style(ui): fix sidebar overflow on smaller screens
chore(config): update MySQL connection timeout property
docs(readme): add Course entity to planned modules table

**Rules:**
- Present tense, imperative mood: "add", not "added" or "adds"
- No period at the end of the subject line
- Keep the subject under 72 characters
- If more context is needed, add a blank line then a body paragraph

---

## Code Style

- Follow existing package structure: `controller`, `service`, `repository`, `entity`, `security`, `config`
- One entity per file, one controller per domain
- Services do the business logic — controllers stay thin
- No raw SQL unless JPA cannot handle the query
- Thymeleaf templates live in `templates/<role>/` subdirectories
- Shared template pieces go in `templates/fragments/`
- CSS goes in `static/css/main.css` — no inline styles

---

## Setting Up Locally

See [README.md](README.md) for full setup instructions.

Quick start:

```bash
git clone <repo-url>
cd pup-sis
# configure application.properties with your MySQL password
./mvnw spring-boot:run
```

---

## Definition of Done

A task is **Done** when:

- [ ] The feature works end-to-end in the browser
- [ ] No compilation errors or warnings
- [ ] The application starts without errors
- [ ] Test accounts (admin/faculty/student) still work correctly
- [ ] Code follows the existing layered structure
- [ ] Commit messages follow the convention above
- [ ] PR has been reviewed and merged to `dev`

