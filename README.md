# Unilend Core - Digital Wallet & P2P Lending System

[![Unilend CI/CD](https://github.com/TienDatNguyen104473172/UNILEND-CORE-P2P-ENGINE-/actions/workflows/deploy.yml/badge.svg)](https://github.com/TienDatNguyen104473172/UNILEND-CORE-P2P-ENGINE-/actions/workflows/deploy.yml)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen)](https://github.com/TienDatNguyen104473172/UNILEND-CORE-P2P-ENGINE-/actions)
[![Docker Hub](https://img.shields.io/badge/Docker-Hub-blue)](https://hub.docker.com/r/tiendatnguyen/unilend-core)

A robust core system for digital wallet transactions and Peer-to-Peer (P2P) lending, ensuring high accuracy, security, and performance for financial operations.

---

## 📖 Table of Contents
1. [Tech Stack](#-tech-stack)
2. [Key Features](#-key-features)
3. [Project Structure](#-project-structure)
4. [Architecture & Database Design](#-architecture--database-design)
5. [Getting Started](#-getting-started)
6. [API Documentation](#-api-documentation)
7. [CI/CD Pipeline](#-cicd-pipeline)

---

## 🛠 Tech Stack
Built with modern technologies to ensure scalability and maintainability:

*   **Core:** ![Java](https://img.shields.io/badge/Java-21-orange?logo=java) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-green?logo=springboot)
*   **Database:** ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql) ![Flyway](https://img.shields.io/badge/Flyway-Migration-red?logo=flyway)
*   **DevOps:** ![Docker](https://img.shields.io/badge/Docker-Enabled-blue?logo=docker) ![Docker Compose](https://img.shields.io/badge/Docker_Compose-Supported-blue?logo=docker) ![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-CI/CD-black?logo=githubactions)
*   **Tools:** ![Maven](https://img.shields.io/badge/Maven-Project_Management-C71A36?logo=apachemaven) ![Postman](https://img.shields.io/badge/Postman-API_Testing-FF6C37?logo=postman) ![Lombok](https://img.shields.io/badge/Lombok-Boilerplate_Reduction-red)

---

## ✨ Key Features
The system implements core fintech business logic:

*   **Authentication (Auth):** Secure Signup and Login using JWT (JSON Web Tokens). Automatic wallet creation upon successful registration.
*   **Wallet Management:**
    *   **Deposit:** Add funds to the user's personal wallet.
    *   **Withdraw:** Securely withdraw funds with balance verification.
    *   **Transfer:** Peer-to-Peer (P2P) fund transfers between users.
*   **Transaction History:** Every balance change is recorded in a Ledger for transparency and auditability.
*   **Concurrency Control (Optimistic Locking):** Uses versioning to prevent Race Conditions during simultaneous transactions.

---

## 📂 Project Structure
A well-organized directory structure following clean architecture principles:

```text
unilend-core/
├── .github/workflows/      # CI/CD pipeline configurations (GitHub Actions)
├── postman/                # API Collection & Environment files for testing
├── src/
│   ├── main/
│   │   ├── java/com/unilend/
│   │   │   ├── common/
│   │   │   │   ├── config/     # Global configurations (Security, etc.)
│   │   │   │   └── enums/      # System-wide enumerations (TransactionType)
│   │   │   ├── controller/     # REST API Endpoints (Controllers)
│   │   │   ├── dto/
│   │   │   │   ├── request/    # Data Transfer Objects for incoming requests
│   │   │   │   └── response/   # Data Transfer Objects for outgoing responses
│   │   │   ├── entity/         # Database Models (JPA Entities)
│   │   │   ├── exception/      # Global Exception Handling & Custom Errors
│   │   │   ├── repository/     # Data Access Layer (Spring Data JPA)
│   │   │   ├── security/       # JWT Implementation & Spring Security filters
│   │   │   └── service/        # Core Business Logic implementation
│   │   └── resources/
│   │       ├── db/migration/   # Flyway SQL migration scripts (V1, V2...)
│   │       └── application.properties # Main application configurations
│   └── test/                   # Unit & Integration tests
├── Dockerfile              # Docker image definition for the application
├── docker-compose.yml      # Orchestration for App + PostgreSQL
└── pom.xml                 # Maven dependencies and build configuration
```

---

## 🏗 Architecture & Database Design
The system follows a **Ledger-based model** - the industry standard for Fintech systems:

### Core Entities:
1.  **`users`**: Stores user identity and credentials.
2.  **`wallets`**: Manages balances, currency types, and `version` for optimistic locking.
3.  **`ledger_transactions`**: The "Heart" of the system. Records every single movement of funds (Deposit, Withdrawal, Transfer). This ensures data integrity and allows for complete balance reconstruction and auditing.

> **Note:** Detailed schema definitions can be found in: `src/main/resources/db/migration/V1__init_schema.sql`

---

## 🚀 Getting Started

### Option 1: Run with Docker (Recommended for Reviewers)
Prerequisites: Docker and Docker Compose installed.

1.  Open your Terminal in the project root.
2.  Run the command:
    ```bash
    docker-compose up -d
    ```
3.  **Access Information:**
    *   API URL: `http://localhost:8088`
    *   Database Port: `5432`
    *   Database Password: `password123`

### Option 2: Run Manually (For Developers)
Prerequisites: Java 21, Maven, PostgreSQL.

1.  Create a database named `unilend` in your PostgreSQL instance.
2.  Configure environment variables or edit `src/main/resources/application.properties`:
    ```properties
    DB_URL=jdbc:postgresql://localhost:5432/unilend
    DB_USERNAME=your_username
    DB_PASSWORD=your_password
    ```
3.  Run the application using Maven:
    ```bash
    mvn spring-boot:run
    ```

---

## 📑 API Documentation

### 1. Testing with Postman
You can find the Collection and Environment files in the `postman/` directory:
- **Collection:** `Unilend Demo.postman_collection.json`
- **Environment:** `Unilend Env(local host).postman_environment.json`

### 2. Main Endpoints

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/signup` | Register a new user | No |
| `POST` | `/api/auth/login` | Login and receive JWT Token | No |
| `POST` | `/api/wallet/deposit` | Deposit funds into wallet | Yes (JWT) |
| `POST` | `/api/wallet/withdraw` | Withdraw funds from wallet | Yes (JWT) |
| `POST` | `/api/wallet/transfer` | Transfer funds to another user | Yes (JWT) |
| `GET` | `/api/wallet/transactions` | View transaction history | Yes (JWT) |

---

## 🔄 CI/CD Pipeline
The project integrates GitHub Actions to automate the deployment workflow:

1.  **Push Code**: Triggered on push to `master` branch.
2.  **Auto Build**: Runs `mvn clean package` to verify compilation and tests.
3.  **Docker Build**: Packages the application into a Docker Image.
4.  **Push to Docker Hub**: Uploads the image to Docker Hub (`tiendatnguyen/unilend-core`).

🔗 View Docker Image: [tiendatnguyen/unilend-core](https://hub.docker.com/r/tiendatnguyen/unilend-core)

---
*Developed by **Tien Dat Nguyen**. Thank you for reviewing!*
