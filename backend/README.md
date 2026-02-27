# DevPath AI - Backend Service

## Overview

This is the Spring Boot backend for DevPath AI.

It is responsible for:

- User authentication (JWT-based)
- Resume upload (PDF)
- PDF text extraction (Apache PDFBox)
- Sending extracted resume text to AI Service
- Returning structured role analysis results

---

## Architecture

Client → Spring Boot → AI Service (FastAPI)  
↓  
PostgreSQL

The backend does NOT perform role analysis logic.
It delegates skill scoring to the AI service.

---

## Tech Stack

- Java 17+
- Spring Boot
- Spring Security (JWT)
- PostgreSQL
- Apache PDFBox
- RestTemplate (AI service communication)

---

## Main Responsibilities

### 1. Authentication
- Register / Login
- JWT token generation
- Secure endpoints

### 2. Resume Upload
- Accepts PDF file
- Extracts clean text
- Sends text to AI service
- Returns analysis JSON

---

## Future Improvements (Post V1)

- Persist Resume & Analysis entities
- Add resume history endpoint
- Improve error handling
- Connect to frontend UI

---

## Running Locally

1. Start PostgreSQL
2. Configure `application.properties`
3. Run:
   ./mvnw spring-boot:run


Backend runs on: http://localhost:8080

## Notes

This backend follows a clean separation of concerns:

- Business logic (Java)
- Intelligence engine (Python AI service)
