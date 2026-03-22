# Architecture Document — Secure Application Design

**Author:** Diego Rozo  
**Course:** Taller de Desarrollo de Software Empresarial (TDSE)  
**Institution:** Escuela Colombiana de Ingenieria Julio Garavito  
**Date:** March 2026

---

## 1. General Description

This application implements a secure two-tier web architecture deployed on AWS. The system guarantees integrity, authentication, and authorization at both the user and server levels through the use of TLS/HTTPS, digital certificates issued by Let's Encrypt, and password hashing with BCrypt.

The architecture follows the principles of the 12-Factor App methodology, separating configuration from code using environment variables, and applies clean code best practices documented in the project's GitHub repository.

---

## 2. System Components

### 2.1 ApacheServer (Frontend Server)

Responsible for serving the HTML+JavaScript web client asynchronously. Acts as the entry point for the end user.

- **Technology:** Apache HTTP Server 2.4 on Amazon Linux 2023
- **Domain:** tdseecidiegorozo.duckdns.org
- **AWS Instance:** EC2 t3.micro (i-0670505f4205069d5)
- **Public IP:** 52.204.183.42
- **Exposed ports:** 80 (HTTP redirect), 443 (HTTPS)
- **TLS Certificate:** Let's Encrypt (valid until June 2026)
- **Served content:** index.html with asynchronous JavaScript client

### 2.2 BackEndServer (REST Services Server)

Exposes the REST authentication and business services. All communication occurs over HTTPS.

- **Technology:** Spring Boot 3.2.4 with embedded Tomcat, Java 17
- **Domain:** tdscecidiegozo2.duckdns.org
- **AWS Instance:** EC2 t3.micro (i-041e83c1f2d7282b2)
- **Public IP:** 3.87.79.120
- **Port:** 5000 (HTTPS)
- **TLS Certificate:** PKCS12 generated from Let's Encrypt
- **Database:** H2 in-memory (development)

### 2.3 Asynchronous Web Client

The frontend is a Single Page Application (SPA) served by Apache. It communicates with the BackEndServer through asynchronous fetch() calls over HTTPS, without ever reloading the full page.

- User registration via POST /auth/register
- User login via POST /auth/login
- Error handling and loading state management in the UI

---

## 3. Architecture Diagram

```
[ BROWSER / USER ]
        |
   HTTPS :443 (TLS - Let's Encrypt)
        |
[ APACHE SERVER - EC2 t3.micro ]
  tdseecidiegorozo.duckdns.org
        |
   HTTPS :5000 (TLS - PKCS12)
        |
[ SPRING BOOT SERVER - EC2 t3.micro ]
  tdscecidiegozo2.duckdns.org:5000
        |
[ H2 IN-MEMORY DATABASE ]
```

---

## 4. Security Mechanisms

| Mechanism | Description | Component |
|---|---|---|
| TLS/HTTPS | Encryption in transit for all communications | Apache + Spring Boot |
| Let's Encrypt | Publicly trusted digital certificates, free and automatically renewable | Both servers |
| PKCS12 Keystore | Standard format for storing keys and certificates in Spring Boot | BackEndServer |
| BCrypt Hashing | Passwords are never stored in plain text; BCrypt hashes with salt are used | Spring Boot |
| CORS Policy | Only requests from the ApacheServer origin are allowed to reach the BackEndServer | Spring Boot |
| Spring Security | Security layer that manages access to application endpoints | Spring Boot |
| 12-Factor Config | Credentials and paths are read from environment variables, never from code | Spring Boot |

---

## 5. Authentication Flow

### 5.1 User Registration

1. The user enters credentials in the HTML form on the ApacheServer
2. The JS client sends POST /auth/register to the BackEndServer via asynchronous fetch()
3. Spring Boot validates that the user does not already exist in the database
4. BCryptPasswordEncoder generates a secure hash of the password with a random salt
5. The hash (never the plain text password) is stored in H2
6. A 200 OK response is returned to the client

### 5.2 User Login

1. The client sends credentials to the POST /auth/login endpoint
2. Spring Boot looks up the user in the database by username
3. BCryptPasswordEncoder.matches() compares the entered password with the stored hash
4. If they match: 200 response with success message
5. If they do not match: 401 Unauthorized response

---

## 6. AWS Infrastructure

| Resource | Value |
|---|---|
| Region | us-east-1 (N. Virginia) |
| Availability Zone | us-east-1a |
| Instance Type | t3.micro (both) |
| Operating System | Amazon Linux 2023 |
| ApacheServer ID | i-0670505f4205069d5 |
| BackEndServer ID | i-041e83c1f2d7282b2 |
| Dynamic DNS | DuckDNS (free) |
| Certificates | Let's Encrypt (90 days, auto-renewable) |

---

## 7. 12-Factor App Principles

The project implements the following factors from the 12-Factor App methodology:

- **Factor I - Codebase:** Code versioned in a single GitHub repository
- **Factor III - Config:** Port, keystore path, and password read from environment variables (`${PORT}`, `${KEY_STORE_PATH}`, `${KEY_STORE_PASSWORD}`)
- **Factor IV - Backing Services:** H2 database treated as an attachable, configurable resource
- **Factor VI - Processes:** The application is stateless; no state is stored between requests
- **Factor VII - Port Binding:** Spring Boot exposes the service directly on the configured port without an external server

---

## 8. Technologies Used

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Backend programming language |
| Spring Boot | 3.2.4 | Web and security framework |
| Spring Security | 6.x | Authentication and authorization |
| Spring Data JPA | 3.x | User persistence |
| H2 Database | 2.x | In-memory database |
| BCrypt | - | Password hashing |
| Maven | 3.x | Dependency management and build lifecycle |
| Apache HTTP Server | 2.4 | Frontend web server |
| Let's Encrypt / Certbot | 2.6 | TLS certificate issuance |
| AWS EC2 | t3.micro | Cloud infrastructure |
| DuckDNS | - | Free dynamic DNS |