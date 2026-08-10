# PayCore — Payroll & HR Management System

PayCore is a complete, full-stack Human Capital Management (HCM) platform built for managing organization employee records, salary structures, leave requests, monthly payslip generation, and financial reporting.

---

## Technical Stack

- **Frontend:** Angular v17, Bootstrap 5, RxJS, Responsive Light Enterprise Design
- **Backend:** Java 17, Spring Boot 3.2, Spring Security (JWT), Hibernate (JPA), iText PDF, OpenCSV
- **Database:** PostgreSQL (Docker / Production) / H2 In-Memory (Local Dev)
- **DevOps:** Docker, Nginx, Docker Compose

---

## Project Features

### 1. Role-Based Access Control (RBAC)
- **Admin (`ROLE_ADMIN`):** Full CRUD on employees, salary structures, leave approvals, and payslip generation.
- **Employee (`ROLE_EMPLOYEE`):** Self-profile view, view/download personal payslips, apply for leave.
- **Accountant (`ROLE_ACCOUNTANT`):** Access financial reports, filter payslip records, export CSV and PDF reports.

### 2. Core Modules
- **Employee Management:** Full employee CRUD, auto-generated Employee IDs (`EMP-1001`), date picker for DOB, unique mobile number verification, designation/department dropdowns, automatic welcome notification.
- **Salary & Payslip Service:** Admin salary structure configuration (Basic, HRA, Allowances, Medical, PF, Tax), monthly payslip generation with automatic **Unpaid Leave Deductions** calculation (`Daily Rate = Gross / 30 * Unpaid Days`). Interactive printable payslip modal.
- **Leave Management:** Employee leave application form (Paid, Unpaid, Sick, Casual), manager approval/rejection interface, in-app notification dispatch.
- **Financial Reports:** Accountant filtering engine (Month/Year, Date Range, Employee filter) with CSV and PDF export capability.

---

## Pre-Populated Credentials (Seed Data)

The application automatically seeds initial accounts and demo data on startup:

| Role | Email | Password | Default Employee Code |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@paycore.com` | `Password123!` | `EMP-1001` |
| **Employee** | `employee@paycore.com` | `Password123!` | `EMP-1002` |
| **Accountant** | `accountant@paycore.com` | `Password123!` | `EMP-1003` |

---

## Quickstart Guide

### Option 1: Run with Docker Compose (Recommended)

To launch the complete application stack (PostgreSQL, Spring Boot backend, Angular frontend via Nginx):

```bash
docker-compose up --build
```

Access ports:
- **Frontend App:** [http://localhost:80](http://localhost:80)
- **Backend REST API:** [http://localhost:8080/api](http://localhost:8080/api)

---

### Option 2: Local Development Setup

#### 1. Backend (Spring Boot)
Requires JDK 17+:

```bash
cd backend
# Run Spring Boot (uses in-memory H2 database by default)
./mvnw spring-boot:run
```

- Backend API running on `http://localhost:8080`
- H2 Database Console accessible at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:paycoredb`, Username: `sa`, Password: empty)

#### 2. Frontend (Angular)
Requires Node.js 18+ and npm:

```bash
cd frontend
npm install
npm start
```

Access the Angular dev server at [http://localhost:4200](http://localhost:4200).

---

## Running Unit Tests

### Backend Unit Tests (JUnit 5 & Mockito)
```bash
cd backend
./mvnw test
```

### Frontend Unit Tests (Jasmine & Karma)
```bash
cd frontend
npm test
```
