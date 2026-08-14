# Bug Tracker API

A backend-only, Jira-style bug/issue tracking API built with Spring Boot. Teams create projects, report issues, assign them to developers, and move them through a controlled status workflow (with role-based access at every step).

Built the project to demonstrate core Spring Boot backend skills — layered architecture, JWT authentication, role-based authorization, relational data modeling, and a real business rule (status-transition validation) rather than plain CRUD.

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Spring Web, Spring Data JPA, Spring Security
- JWT authentication (jjwt)
- MySQL (default) 
- Lombok
- springdoc-openapi (Swagger UI)
- JUnit 5 + Mockito

## Features

- **JWT-based auth** — register, login, stateless token-based sessions
- **Role-based access control** — ADMIN, DEVELOPER, REPORTER, with different permissions per endpoint
- **Project & issue management** — full CRUD with pagination and filtering
- **Status workflow enforcement** — an issue can't skip states; transitions are validated against an explicit state machine (`OPEN → IN_PROGRESS → RESOLVED → CLOSED`, with a reopen path from RESOLVED back to IN_PROGRESS)
- **Comments** — threaded discussion per issue
- **Admin-only role management** — new users always register as REPORTER; promotion to DEVELOPER/ADMIN is a separate protected endpoint, preventing self-assigned privilege escalation
- **Interactive API docs** via Swagger UI

### Prerequisites
- JDK 17
- Maven
- MySQL running locally

### Run with MySQL (default)
1. Make sure MySQL is running locally with a `root` user (or set the `DB_USERNAME` / `DB_PASSWORD` env vars to match your setup)
2. Clone the repo and run:
   mvn spring-boot:run
3. The app auto-creates the `bugtracker` database and all tables on first run.

The app starts on `http://localhost:8080`.

### Run tests
mvn test

## API Documentation

Once running, open:
http://localhost:8080/swagger-ui.html

All endpoints are interactive — register/login to get a JWT, click **Authorize** and paste the token, then try any endpoint directly from the browser
