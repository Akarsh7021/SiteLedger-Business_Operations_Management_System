# Employee Tracking and Payment Management

Local desktop-style Spring Boot application for managing employees and payroll records for a small business.

## Phase 1 Scope

This first phase sets up the foundation:

- Spring Boot application structure
- Form-based admin login with BCrypt password encoding
- Local-only server binding on `127.0.0.1`
- SQLite database storage
- Employee create, read, update, delete, and search
- Server-rendered HTML pages using Thymeleaf

## Architecture

The code is organized by feature and responsibility:

- `config`: application-wide configuration, currently Spring Security
- `home`: login and root routing
- `employee`: employee entity, form DTO, repository, service, and controller
- `resources/templates`: Thymeleaf pages
- `resources/static`: CSS and browser assets

The controller handles web requests, the service owns business rules, the repository owns database access, and the entity represents the database table.

## SQLite

The app uses a local SQLite file:

```properties
spring.datasource.url=jdbc:sqlite:employee-payroll.db
```

The database file is ignored by Git because it contains local business data.

## Login

Default development login:

```text
Username: admin
Password: change-me-now
```

Change `app.security.admin-password` in `src/main/resources/application.properties` before storing real employee information. Environment variables can also override Spring properties later when packaging the desktop app.

## Run

From IntelliJ, import the project as a Maven project and run:

```text
com.familybusiness.payroll.EmployeeTrackingApplication
```

The app opens at:

```text
http://127.0.0.1:8080
```

## Next Phases

1. Move admin credentials into the database with a setup screen.
2. Add work hour tracking.
3. Add payroll period calculation.
4. Add payment history.
5. Add weekly/monthly reports.
6. Package as a desktop application.
