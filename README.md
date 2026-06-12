# Employee Tracking and Payment Management

Local desktop-style Spring Boot application for managing employees and payroll records for a small business.

## Phase 1 Scope

This first phase sets up the foundation:

- Spring Boot application structure
- Form-based admin login with BCrypt password encoding
- Local-only server binding on `127.0.0.1`
- SQLite database storage
- Employee create, read, update, delete, and search
- Work-hour create, read, update, delete, employee filtering, and paid/unpaid tracking
- Server-rendered HTML pages using Thymeleaf

## Architecture

The code is organized by feature and responsibility:

- `config`: application-wide configuration, currently Spring Security
- `home`: login and root routing
- `employee`: employee entity, form DTO, repository, service, and controller
- `workhour`: daily work-hour records connected to employees
- `resources/templates`: Thymeleaf pages
- `resources/static`: CSS and browser assets

The controller handles web requests, the service owns business rules, the repository owns database access, and the entity represents the database table.

## SQLite

The app uses a local SQLite file:

```properties
spring.datasource.url=jdbc:sqlite:employee-payroll.db
```

The database file is ignored by Git because it contains local business data. A fresh database is created from `src/main/resources/schema.sql` when the app starts.

## Google Location Search

Work-site location search can use Google Places Autocomplete for more accurate local address suggestions. Enable Places API (New) in Google Cloud, then set:

```bash
export GOOGLE_MAPS_API_KEY=your_api_key_here
```

On Windows PowerShell:

```powershell
$env:GOOGLE_MAPS_API_KEY="your_api_key_here"
```

If no key is set, the app falls back to OpenStreetMap search.

## Login

Default development login:

```text
Username: admin
Password: change-me-now
```

Change `app.security.admin-password` in `src/main/resources/application.properties` before storing real employee information. Environment variables can also override Spring properties later when packaging the desktop app.

## Run

Install Java 17 or newer. On macOS, a simple option is:

```bash
brew install openjdk@17
```

From IntelliJ, import the project as a Maven project and run:

```text
com.familybusiness.payroll.EmployeeTrackingApplication
```

The app opens at:

```text
http://127.0.0.1:8081
```

From a terminal on macOS or Linux:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

From PowerShell on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## Package For Local Use

To create a portable Windows app folder that can be copied to another laptop:

```powershell
.\scripts\package-portable-windows.ps1
```

The output is created at:

```text
dist\SiteLedger
```

Give the whole `SiteLedger` folder to the user. They can run:

```text
Start SiteLedger.bat
```

On macOS or Linux, create the same portable folder with:

```bash
chmod +x scripts/package-portable-unix.sh
./scripts/package-portable-unix.sh
```

The app data is stored in `employee-payroll.db` inside the portable folder.

## Next Phases

1. Move admin credentials into the database with a setup screen.
2. Add payroll period calculation.
3. Move payment records into a full payment history table.
4. Add weekly/monthly reports.
5. Package as a desktop application.
