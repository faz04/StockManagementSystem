# Setup Guide

## Database Setup

### 1. Install SQL Server
Download and install [SQL Server Express](https://www.microsoft.com/en-us/sql-server/sql-server-downloads) (free version).

### 2. Create the database and user
Run the following in SQL Server Management Studio:

```sql
CREATE DATABASE stock_management_DB;

-- Create a login with your own username and password
CREATE LOGIN your_username WITH PASSWORD = 'your_secure_password';
USE stock_management_DB;
CREATE USER your_username FOR LOGIN your_username;
ALTER ROLE db_owner ADD MEMBER your_username;
```

### 3. Configure application properties
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Then edit `application.properties` and fill in your actual DB username and password.

## Running the Application
```bash
mvn spring-boot:run
```

Access at: `http://localhost:8080`
