# Distributed Project Management System (Distributed PMS)

A distributed microservices system built using **Spring Boot**, **RabbitMQ**, and **PostgreSQL**, implementing **Saga-based asynchronous orchestration** for managing cross-service workflows.

The project demonstrates real-world backend architecture patterns, including **database-per-service isolation**, **event-driven communication**, and **fault-tolerant transaction management** using compensating actions.

The system is fully containerized with **Docker Compose**, enabling reproducible local setups and service independence.  
It showcases **loose coupling**, **eventual consistency**, and **resilience** in a multi-service environment, along with clear separation of responsibilities across services.

---

## 📌 Table of Contents

1. Overview
2. Tech Stack
3. Architecture
4. Services Description
5. Messaging Flow (Saga‑style)
6. Folder Structure
7. Configuration Strategy (.env & application.yml)
8. Docker & Containerization
9. Installation & Setup
10. Running the Application
11. Service URLs & Ports
12. Common Errors & Fixes
13. TestCase Scenario (TestCase File)

---

## 1️⃣ Overview

The **Distributed PMS** is designed as a **microservices‑based backend system** where each service:

* Owns its **own database**
* Communicates **asynchronously** using RabbitMQ
* Is independently deployable

This architecture avoids tight coupling and enables scalability, fault tolerance, and clear separation of concerns.

---

## 2️⃣ Tech Stack

| Layer            | Technology                  |
| ---------------- |-----------------------------|
| Language         | Java 17                     |
| Framework        | Spring Boot 3.2.2           |
| Messaging        | RabbitMQ                    |
| Databases        | PostgreSQL (per service)    |
| ORM              | Spring Data JPA / Hibernate |
| Build Tool       | Gradle                      |
| Containerization | Docker, Docker Compose      |

---

## 3️⃣ Architecture

### High‑Level Architecture Diagram

```
                     ┌──────────────────────┐
                     │   Client / Frontend  │
                     └───────────┬──────────┘
                                 │ 
         ┌───────────────────────┼────────────────────────┐
         │                       │                        │
┌────────▼────────┐   ┌─────────▼────────┐   ┌──────────▼─────────┐
│  User Service    │   │  Task Service     │   │  Project Service   │
│  (8082)          │   │  (8081)           │   │  (8083)            │
│  user_db         │   │  task_db           │   │  project_db        │
└────────┬─────────┘   └─────────┬─────────┘   └──────────┬─────────┘
         │                       │                        │
         └──────────────┬────────┴────────┬──────────────┘
                        │   RabbitMQ       │
                        │   (5672/15672)   │
                        └────────┬─────────┘
                                 │
                       ┌─────────▼─────────┐
                       │ Notification svc  │
                       │                   │
                       │ (8084)            │
                       └───────────────────┘
```

### Key Architectural Decisions

* **Database‑per‑service** (no shared DBs)
* **Async messaging** instead of REST chaining
* **Loose coupling via events**

---

## 4️⃣ Services Description

### 🧑 User Service

* Manages users
* Owns `user_db`
* Publishes user‑related events

### 📋 Task Service

* Manages tasks
* Owns `task_db`
* Listens to user/project events

### 📁 Project Service

* Manages projects
* Owns `project_db`
* Publishes project lifecycle events

### 🔔 Notification Service

* Listens to events from RabbitMQ
* Sends notifications (future: email / websocket)

---

## 5️⃣ Messaging Flow (Saga‑Style)

Example: **Project Creation Flow**

1. Client creates project via Project Service
2. Project Service saves data
3. Project Service publishes `PROJECT_CREATED` event
4. Task Service consumes event and initializes tasks
5. Notification Service sends notification

✔ No direct REST calls between services
✔ Failure isolation

---

## 6️⃣ Folder Structure

```
Distributed-PMS/
│
├── TaskService/
│   ├── Dockerfile
│   └── src/
│
├── UserService/
│   ├── Dockerfile
│   └── src/
│
├── ProjectService/
│   ├── Dockerfile
│   └── src/
│
├── NotificationService/
│   ├── Dockerfile
│   └── src/
│
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## 6️⃣.1 Postman Request Examples


All saga workflows are triggered via the **Task Service**, which acts as the saga orchestrator.

**Base URL**
```
POST : http://localhost:8081/tasks  (Task Service)
```
**Request Body**
```json
{
  "title": "Build Authentication Module",
  "userId": "u1",
  "projectId": "p1"
}

```

---

## 7️⃣ Configuration Strategy

### application.yml (All Services)

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}

  rabbitmq:
    host: ${SPRING_RABBITMQ_HOST}
    port: ${SPRING_RABBITMQ_PORT}
    username: ${SPRING_RABBITMQ_USERNAME}
    password: ${SPRING_RABBITMQ_PASSWORD}
```

✔ No secrets committed to GitHub
✔ Environment‑driven config

---

## 8️⃣ Docker & Containerization

### docker-compose.yml Responsibilities

* Creates isolated network
* Starts RabbitMQ
* Starts PostgreSQL (3 instances)
* Builds & runs all services

Each service connects using **container name as hostname**.

---

## 9️⃣ Installation & Setup

### Prerequisites

* Docker >= 24.x
* Docker Compose v2
* Internet connection (for first build)

### Data Prerequisites

The **User Service** and **Project Service** databases must contain pre-inserted data
for the saga workflows to execute successfully.

- Valid `userId` values must already exist in the **user_db**
- Valid `projectId` values must already exist in the **project_db**

These records are required to:
- Validate happy-path execution
- Simulate failure scenarios using non-existent IDs

> Note: Docker volumes preserve database state across restarts.
If required, databases can be reset by removing volumes and restarting the services.



The following sample records must exist in the **User Service** and **Project Service**
databases to execute saga workflows correctly.

#### User Service – `user_db`

```sql
-- status is an enum (ACTIVE) and can be NULL
INSERT INTO users (id, name, status)
VALUES
    ('u1', 'Alice Sharma', NULL),
    ('u2', 'Bob Verma', NULL);

```
#### Project Service – `project_db`
```sql
INSERT INTO projects (id, name)
VALUES
('p1', 'Distributed PMS'),
('p2', 'Internal Tooling');
```


### Clone Repository

```bash
git clone <https://github.com/NamanGarg11/Distributed-PMS-Microservice-Based->
cd Distributed-PMS-Microservice-Based-
```

---

## 🔟 Running the Application

### Build Images (first time)

```bash
docker compose build
```

### Start All Services

```bash
docker compose up
```

### Stop Services

```bash
docker compose down
```

---

## 1️⃣1️⃣ Service URLs & Ports

| Service              | Port                                             |
| -------------------- | ------------------------------------------------ |
| Task Service         | 8081                                             |
| User Service         | 8082                                             |
| Project Service      | 8083                                             |
| Notification Service | 8084                                             |
| RabbitMQ UI          | [http://localhost:15672](http://localhost:15672) |

RabbitMQ Credentials:

* Username: guest
* Password: guest

---

## 1️⃣2️⃣ Common Errors & Fixes

### ❌ exec format error (Postgres)

**Cause:** Architecture mismatch
**Fix:**

```bash
docker pull postgres:15
```

### ❌ UnknownHostException: postgres‑user

**Cause:** Service started before DB
**Fix:** Use `depends_on` (already configured)

### ❌ Slow first build

**Cause:** Gradle dependency download
**Fix:** Normal behavior — cache improves next builds


---



## 1️⃣3️⃣ Test Case Scenarios
All test cases are triggered via the `POST /tasks` endpoint of the Task Service.


## Test Case 1: Happy Path (Corrected with Notification)

```
Client
  |
  |  POST /tasks
  v
TaskService
  |
  |=> TASK_CREATED
  v
UserService
  |
  |=> USER_VALIDATED
  v
ProjectService
  |
  |=> PROJECT_VALIDATED
  v
NotificationService
  |
  |=> NOTIFICATION_SENT
  v
TaskService
  |
  |  Mark Task COMPLETED
  v
Database
```

---

## Test Case 2: User Service Failure

```
Client
  |
  |  POST /tasks
  v
TaskService
  |
  |=> TASK_CREATED
  v
UserService
  |
  X  USER_NOT_FOUND
  |
  |=> USER_VALIDATION_FAILED
  v
TaskService
  |
  |⟲ TASK_COMPENSATION
  |  Mark Task FAILED
  v
Database
```

---

## Test Case 3: Project Service Failure

```
Client
  |
  |  POST /tasks
  v
TaskService
  |
  |=> TASK_CREATED
  v
UserService
  |
  |=> USER_VALIDATED
  v
ProjectService
  |
  X  PROJECT_INVALID
  |
  |=> PROJECT_VALIDATION_FAILED
  v
TaskService
  |
  |⟲ TASK_COMPENSATION
  |  Mark Task FAILED
  v
Database
```
## Test Execution & Validation Evidence

The Saga orchestration flow has been tested against all defined scenarios.
A detailed test execution report including inputs, outputs, logs, and observations
is available at the link below.

📄 **Test Report:**  
👉 [View Test Execution Report](https://docs.google.com/document/d/1xZRqWo5J1kJMokr5zM4ZL6GMYYYHqX-n/edit?usp=drive_link)

### Covered Scenarios
- ✅ Happy Path execution
- ❌ UserService failure & rollback
- ❌ ProjectService failure & compensation

### Testing Notes
- Automated test coverage is limited; the focus of this task was on system design,
  saga orchestration, and integration-level validation.
- End-to-end workflows were manually tested and validated using Postman and service logs.


---



