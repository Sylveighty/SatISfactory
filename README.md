# SatISfactory

A web-based Student Information System (SIS) built with Spring Boot, Thymeleaf, and MySQL.
Developed as a proof-of-concept finals project for the Polytechnic University of the Philippines - San Pedro Campus.

---

## Features 

- Role-based login system (Admin, Faculty, Student)
- Automatic dashboard routing after login based on role
- Persistent sidebar navigation scoped to each role
- PUP maroon branded UI, no external CSS frameworks
- BCrypt password hashing via Spring Security
- Database seeding with default test accounts

## Planned Modules

| Module | Role | Status |
|---|---|---|
| Login & role routing | All | ✅ Done |
| Role dashboards | All | ✅ Done |
| Student CRUD | Admin | 🔲 Step 2 |
| Faculty CRUD | Admin | 🔲 Step 2 |
| Curriculum management | Admin | 🔲 Step 2 |
| Subject management | Admin | 🔲 Step 3 |
| Section management | Admin | 🔲 Step 3 |
| Schedule management | Admin | 🔲 Step 4 |
| Grade encoding | Faculty | 🔲 Step 4 |
| Student enrollment | Student | 🔲 Step 5 |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 (OpenJDK 17) |
| Framework | Spring Boot 3.5.14 |
| Build | Maven |
| Security | Spring Security 6 |
| Persistence | Spring Data JPA / Hibernate |
| Database | MySQL 8 |
| Templates | Thymeleaf 3 |
| Frontend | Plain HTML/CSS (no JS frameworks currently) |
| Editor | VS Code |

---

## Project Structure

```text
src/main/
├── java/com/pup/sis/
│   ├── SisApplication.java       # Entry point
│   ├── config/
│   │   └── DataSeeder.java       # Seeds default users on startup
│   ├── controller/
│   │   ├── AuthController.java   # /login, /
│   │   └── DashboardController.java
│   ├── entity/
│   │   ├── Role.java             # ADMIN | FACULTY | STUDENT
│   │   └── User.java             # Core user account entity
│   ├── repository/
│   │   └── UserRepository.java
│   ├── security/
│   │   ├── CustomUserDetailsService.java
│   │   └── SecurityConfig.java   # Filter chain, success handler
│   └── service/
│       └── UserService.java
└── resources/
├── application.properties
├── static/css/main.css
└── templates/
├── login.html
├── fragments/sidebar.html
├── admin/dashboard.html
├── faculty/dashboard.html
└── student/dashboard.html
```

---

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- MySQL 8.0+
- VS Code (recommended) or any IDE

### Installation

**1. Clone the repository**

```bash
git clone https://github.com/Sylveighty/SatISfactory.git
cd SatISfactory
```

**2. Create the database**

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS pup_sis;"
```

**3. Configure your database credentials**

Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.username=root
spring.datasource.password=your_password_here
```

**4. Run the application**

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

**5. Open in your browser**

http://localhost:8080

---

## Default Test Accounts

These are seeded automatically on the first run. Passwords are BCrypt-hashed in the database.

| Role | Username | Password |
|---|---|---|
| Administrator | `admin` | `admin123` |
| Faculty | `faculty1` | `faculty123` |
| Student | `student1` | `student123` |

> Change these in `DataSeeder.java` before any production use.

---

## Architecture

The project follows a standard layered Spring Boot architecture:

Browser
└── Controller          (handles HTTP requests)
└── Service       (business logic, orchestration)
└── Repository (data access via JPA)
└── Entity (maps to database tables)
Security layer intercepts all requests before they reach controllers.
Thymeleaf templates render server-side HTML returned by controllers.

**Key design decisions:**

- One `User` entity covers all roles. Role-specific profile data (student number, department) will be added as separate linked entities in later steps.
- The sidebar is a single Thymeleaf fragment. `sec:authorize` hides/shows nav items per role — no duplicate HTML.
- `AuthenticationSuccessHandler` handles role routing after login, keeping URL mapping clean and explicit.
- `DataSeeder` checks `userRepository.count() > 0` before inserting, making it safe to restart the app.

---

## Branching Strategy

main          production-ready, tagged releases
dev           integration branch - PRs merge here first
feat/<name>   feature branches, branched from dev

**Example flow:**

```bash
git checkout dev
git checkout -b feat/student-crud
# ... do work ...
git push origin feat/student-crud
# open PR to dev
```

---

## Commit Convention

feat(scope): short description of what was added
fix(scope): short description of what was fixed
chore(scope): tooling, config, non-feature work
docs(scope): documentation only
refactor(scope): code change with no behaviour change

Examples from this project:

feat(entity): add User entity and Role enum
feat(security): configure SecurityFilterChain with role-based access
feat(ui): add sidebar fragment with role-based navigation
chore(config): configure MySQL datasource and JPA properties
docs: add README with setup and architecture sections

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for the full development workflow, branch naming rules, and PR checklist.

---

## License

This project is for academic and educational purposes.
Polytechnic University of the Philippines - San Pedro Campus.