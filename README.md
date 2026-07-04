# 🏭 Stock Management System — User Management Module

A production-ready **Spring Boot REST API** for user management in a stock management system. Features role-based access control, JWT authentication, BCrypt password encryption, audit logging, and full CRUD operations.

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.3.0 |
| Security | Spring Security, JWT (jjwt 0.11.5), BCrypt |
| Database | Microsoft SQL Server |
| ORM | Spring Data JPA / Hibernate |
| Validation | Spring Boot Validation |
| Build | Apache Maven |
| Monitoring | Spring Boot Actuator |

---

## 📁 Project Structure

```
src/main/java/com/stockmanagement/usermanagementsystem/
├── config/
│   ├── SecurityConfig.java       ← Spring Security + BCrypt config
│   ├── WebConfig.java            ← CORS configuration
│   └── DataInitializer.java      ← Seed initial data
├── controller/
│   ├── UserController.java       ← User CRUD REST endpoints
│   ├── AuthController.java       ← Login/logout endpoints
│   ├── HealthController.java     ← Health check endpoint
│   └── WebController.java        ← Web page routing
├── dto/
│   ├── request/                  ← CreateUserRequest, UpdateUserRequest, LoginRequest
│   └── response/                 ← ApiResponse, UserResponse, LoginResponse
├── entity/
│   ├── User.java                 ← User entity with audit fields
│   ├── UserRole.java             ← Role enum (ADMIN, STOCK_MANAGER, etc.)
│   ├── UserSession.java          ← Session tracking entity
│   └── UserAuditLog.java         ← Audit trail entity
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── UserNotFoundException.java
│   ├── UserAlreadyExistsException.java
│   └── WeakPasswordException.java
├── repository/
│   ├── UserRepository.java
│   ├── UserSessionRepository.java
│   └── UserAuditLogRepository.java
├── service/
│   ├── UserService.java          ← User management interface
│   ├── AuthService.java          ← Authentication interface
│   ├── AuditLogService.java      ← Audit logging interface
│   └── impl/                     ← Service implementations
└── util/
    ├── PasswordValidator.java    ← Password strength validation
    └── ResponseUtil.java         ← Standardized API responses
```

---

## 👥 User Roles

| Role | Description |
|------|-------------|
| `ADMIN` | Full system access |
| `STOCK_MANAGER` | Stock management operations |
| `SALES_STAFF` | Sales operations |
| `HR_STAFF` | Human resources operations |
| `MARKETING_MANAGER` | Marketing operations |

---

## 📋 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | User login |
| POST | `/api/auth/logout` | User logout |

### User Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/username/{username}` | Get user by username |
| GET | `/api/users/role/{role}` | Get users by role |
| GET | `/api/users/active` | Get active users |
| GET | `/api/users/inactive` | Get inactive users |
| POST | `/api/users` | Create new user |
| PUT | `/api/users/{id}` | Update user |
| PUT | `/api/users/{id}/reset-password` | Reset user password |
| DELETE | `/api/users/{id}` | Deactivate user |
| GET | `/api/users/stats/count` | Get user statistics |

### System
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/test/hello` | Health check |

---

## 🚀 How to Run

### Prerequisites
- Java 17 or above
- Maven 3.6+
- Microsoft SQL Server 2019+ (or SQL Server Express)

### Step 1 — Set up the database
```sql
-- Run in SQL Server Management Studio
CREATE DATABASE stock_management_DB;

CREATE LOGIN your_username WITH PASSWORD = 'your_password';
USE stock_management_DB;
CREATE USER your_username FOR LOGIN your_username;
ALTER ROLE db_owner ADD MEMBER your_username;
```

### Step 2 — Configure application properties
```bash
# Copy the example config
cp src/main/resources/application.properties.example src/main/resources/application.properties

# Edit application.properties and fill in your DB credentials
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Step 3 — Run the application
```bash
mvn spring-boot:run
```

### Step 4 — Verify
```
Application URL:  http://localhost:8080
API Base URL:     http://localhost:8080/api
Health Check:     http://localhost:8080/api/test/hello
```

---

## 📝 Sample API Requests

### Create User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePass123!",
    "role": "STOCK_MANAGER"
  }'
```

### Get All Users
```bash
curl -X GET http://localhost:8080/api/users
```

### Reset Password
```bash
curl -X PUT http://localhost:8080/api/users/1/reset-password \
  -H "Content-Type: application/json" \
  -d '{"newPassword": "NewPass456!"}'
```

---

## 🔒 Security Features

- **BCrypt** password hashing — passwords never stored in plain text
- **JWT** token-based authentication
- **Spring Security** filter chain for endpoint protection
- **Password strength validation** — enforces complexity requirements
- **Audit logging** — every user operation is tracked with timestamp and actor
- **Session management** — user session tracking via `UserSession` entity
- **CORS** configured for API access

---

## 📌 Key Technical Highlights

- Clean **layered architecture** — Controller → Service → Repository
- **DTO pattern** — separates API contracts from database entities
- **Global exception handling** with standardized error responses
- **JPA Auditing** — `@CreatedDate`, `@LastModifiedDate` auto-populated
- **Soft delete** — users deactivated, not permanently deleted
- **Input validation** with `@Valid` and Jakarta Bean Validation

---

## 👨‍💻 Developer

**F R M Fasri** — IT24102049
BSc (Hons) Information Technology, Specialization in Cybersecurity
Sri Lanka Institute of Information Technology (SLIIT)

## Connect with Me

[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/faz04)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/m-fasri/)
