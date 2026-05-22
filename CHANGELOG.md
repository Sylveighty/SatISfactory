# Changelog

All notable changes to the PUP SIS project are documented in this file.
Format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

---

## [0.1.0] — Step 1: Project Bootstrap & Authentication

### Added

**Project Setup**
- Initialized Spring Boot 3.2.5 project with Maven
- Configured Java 17 target in `pom.xml`
- Added `.gitignore` covering Maven output, IDE files, OS artifacts, and local property overrides

**Database & Persistence**
- MySQL 8 datasource configured via `application.properties`
- JPA/Hibernate with `ddl-auto=update` for automatic schema management
- `User` entity with fields: `id`, `username`, `password`, `fullName`, `email`, `role`, `enabled`
- `Role` enum: `ADMIN`, `FACULTY`, `STUDENT`
- `UserRepository` extending `JpaRepository` with `findByUsername` query

**Security**
- Spring Security 6 configured with `SecurityFilterChain`
- `CustomUserDetailsService` loads users from the database for authentication
- BCrypt password encoding via `BCryptPasswordEncoder`
- Role-based URL protection: `/admin/**`, `/faculty/**`, `/student/**`
- `AuthenticationSuccessHandler` routes each role to its dashboard after login
- CSRF protection active (Thymeleaf forms include the token automatically)

**Data Seeding**
- `DataSeeder` seeds three accounts on first startup:
  - `admin` / `admin123` → ADMIN
  - `faculty1` / `faculty123` → FACULTY
  - `student1` / `student123` → STUDENT
- Guard check prevents duplicate seeding on restart

**Controllers**
- `AuthController`: handles `GET /` (redirect) and `GET /login` with error/logout feedback
- `DashboardController`: serves `/admin/dashboard`, `/faculty/dashboard`, `/student/dashboard`

**UI**
- PUP maroon (`#3D0C0C`) branded CSS theme — no external frameworks
- Fixed 240px sidebar with role-scoped navigation via `sec:authorize`
- Thymeleaf sidebar fragment (`fragments/sidebar.html`) reused across all pages
- Login page: centered card, maroon gradient background, error/success alerts
- Dashboard pages: sticky top header with role badge, responsive card grid
- Hover transitions on nav links and dashboard cards

### Architecture Established
- Layered package structure: `controller`, `service`, `repository`, `entity`, `security`, `config`
- Thymeleaf fragment system for shared UI components
- GitHub repo with PR template, issue templates, and contributing guide

---

## [Unreleased] — Step 2: Student & Faculty CRUD (Planned)

### Planned
- `Student` entity linked to `User`
- `Faculty` entity linked to `User`
- `Course` entity (BSIT, BSCS, etc.)
- Admin: student list, add, edit, delete
- Admin: faculty list, add, edit, delete
- Reusable table and form CSS partials
- Flash message system (success/error feedback after mutations)