# Distributed Project Management System (Distributed PMS)

A **Spring Boot + RabbitMQ + PostgreSQL** based **distributed microservices system** implementing **Saga‑style asynchronous communication**. This project demonstrates real‑world microservice patterns including database‑per‑service, event‑driven messaging, containerization with Docker Compose, and service isolation.

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
13. Future Improvements

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
| ---------------- | --------------------------- |
| Language         | Java 17                     |
| Framework        | Spring Boot 3.x             |
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

### Clone Repository

```bash
git clone <repo-url>
cd Distributed-PMS
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

## 1️⃣3️⃣ Future Improvements

* API Gateway
* Centralized Config Server
* Distributed Tracing (Zipkin)
* Authentication (JWT)
* Kubernetes deployment

---

## ✅ Conclusion

This project demonstrates **real‑world distributed system principles** using industry‑standard tooling and clean architectural practices. It is suitable for:

* Final year projects
* Backend internships
* System design interviews

---

✍️ You can freely edit this README to add screenshots, API examples, or diagrams.
