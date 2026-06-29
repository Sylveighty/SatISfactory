# SatISfactory

A modern, web-based Student Information System (SIS) built with **Spring Boot**, **Thymeleaf**, **Spring Security**, and **MySQL**.

Developed for the Polytechnic University of the Philippines San Pedro Campus, SatISfactory reimagines the traditional Student Information System by prioritizing a clean, responsive, and intuitive user experience without sacrificing essential administrative functionality.

Unlike many existing school portals that can feel cluttered or difficult to navigate, SatISfactory focuses on simplicity, accessibility, and efficient workflows for administrators, faculty members, and students.

---

# Why SatISfactory?

Many academic information systems are powerful but often feel outdated, visually overwhelming, and difficult to use.

SatISfactory was designed around three guiding principles:

- **Simple** - clean interfaces that reduce unnecessary complexity
- **Responsive** - optimized for desktop and mobile devices
- **Intuitive** - users can quickly find what they need with minimal clicks

By combining modern UI design with a robust backend architecture, the platform delivers a familiar yet significantly improved user experience.

---

# Features

## Authentication & Security

- Secure role-based login
- Spring Security authentication
- BCrypt password hashing
- Automatic dashboard routing after login
- Password reset via email
- Session-based authentication
- Protected routes and authorization

---

## Administrator Portal

Administrators have complete control over academic records and system management.

### Student Management

- Create student accounts
- Edit student information
- Delete students
- Search student records

### Faculty Management

- Add faculty members
- Edit faculty information
- Remove faculty accounts

### Academic Management

- Curriculum Management
- Subject Management
- Section Management
- Schedule Management

### Communication

- Announcement management
- Internal messaging

---

## Faculty Portal

Faculty members can:

- Access their dashboard
- View assigned schedules
- View class information
- Manage grades
- Read announcements
- Send and receive messages

---

## Student Portal

Students can:

- Access their personal dashboard
- View enrolled subjects
- View schedules
- View grades
- Read announcements
- Access messages

---

# Tech Stack

| Layer | Technology |
|--------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security 6 |
| ORM | Spring Data JPA (Hibernate) |
| Database | MySQL 8 |
| Template Engine | Thymeleaf |
| Build Tool | Maven |
| Email | Spring Boot Mail |
| Utility | Lombok |

---

# Project Structure

```text
src/main/java/com/pup/sis
│
├── config
│   └── Application configuration
│
├── controller
│   ├── Authentication
│   ├── Dashboard
│   ├── Password Reset
│   ├── API Endpoints
│   ├── Administrator Controllers
│   ├── Faculty Portal
│   └── Student Portal
│
├── entity
│   ├── User
│   ├── Student
│   ├── Faculty
│   ├── Course
│   ├── Subject
│   ├── Section
│   ├── Schedule
│   ├── Grade
│   ├── Announcement
│   ├── Message
│   └── PasswordResetToken
│
├── repository
│
├── security
│
└── service
```

---

# Core Domain Model

The application is built around several interconnected entities:

- **User** – Authentication and role management
- **Student** – Student profile and academic information
- **Faculty** – Faculty profile and teaching assignments
- **Course** – Academic programs
- **Subject** – Individual courses offered
- **Section** – Student grouping
- **Schedule** – Class schedules
- **Grade** – Student academic performance
- **Announcement** – System-wide announcements
- **Message** – Internal communication
- **PasswordResetToken** – Secure password recovery

---

# Architecture

SatISfactory follows a layered Spring Boot architecture.

```text
Browser
      │
      ▼
Controllers
      │
      ▼
Services
      │
      ▼
Repositories
      │
      ▼
MySQL Database
```

Each layer has a clearly defined responsibility:

- **Controllers** handle HTTP requests and responses.
- **Services** contain business logic.
- **Repositories** provide database access using Spring Data JPA.
- **Entities** represent database tables and relationships.

Spring Security intercepts incoming requests before they reach the controller layer, ensuring only authorized users can access protected resources.

---

# Getting Started

## Prerequisites

- Java 17+
- Maven
- MySQL 8
- VS Code or IntelliJ IDEA

---

## Installation

### Clone the repository

```bash
git clone https://github.com/Sylveighty/SatISfactory.git
cd SatISfactory
```

### Create the database

```sql
CREATE DATABASE pup_sis;
```

### Configure the database

Update:

```
src/main/resources/application.properties
```

with your MySQL credentials.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pup_sis
spring.datasource.username=root
spring.datasource.password=your_password
```

### Run the application

Linux / macOS

```bash
./mvnw spring-boot:run
```

Windows

```cmd
mvnw.cmd spring-boot:run
```

Then open:

```
http://localhost:8080
```

---

# Default Test Accounts

The application automatically seeds several accounts for development.

| Role | Username | Password |
|------|----------|----------|
| Administrator | admin | admin123 |
| Faculty | faculty1 | faculty123 |
| Student | student1 | student123 |

These accounts are intended for development and demonstration purposes only.

---

# Design Philosophy

SatISfactory was built with user experience as a primary consideration.

Rather than replicating the complexity of many existing academic portals, the project emphasizes:

- Clear navigation
- Consistent interface design
- Responsive layouts
- Role-focused workflows
- Maintainable backend architecture

The result is an information system that is easier to learn, easier to maintain, and more pleasant to use.

---

# Branching Strategy

```
main
│
├── dev
│
└── feature/<feature-name>
```

Feature branches are merged into **dev** before being promoted to **main**.

---

# Commit Convention

```
feat(scope): add new feature
fix(scope): bug fixes
docs(scope): documentation
refactor(scope): code improvements
style(scope): formatting changes
test(scope): testing
chore(scope): maintenance
```

Example:

```
feat(student): implement student CRUD
feat(schedule): add schedule management
fix(auth): resolve login redirect issue
docs(readme): update project documentation
```

---

# Future Improvements

Potential future enhancements include:

- GPA computation
- Student enrollment workflow
- Faculty grade submission approval
- Audit logs
- File uploads
- SMS/email notifications
- Analytics dashboard
- REST API expansion
- Docker deployment
- CI/CD pipeline

---

# Contributing

Contributions, suggestions, and improvements are welcome.

Please see **CONTRIBUTING.md** for the project's development workflow and pull request guidelines.

---

# License

This project was developed for academic and educational purposes as part of the Polytechnic University of the Philippines San Pedro Campus Final Exam for Object Oriented Programming Subject.
