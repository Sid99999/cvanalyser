# 🚀 CVAnalyser

AI-powered CV analysis platform that evaluates resumes against job descriptions using OpenAI and ATS-style scoring.

**Live Demo:**  
https://cvanalyser.vercel.app

**Backend API:**  
https://cvanalyser-backend-production.up.railway.app

---

## 🎯 Overview

CVAnalyser is a full-stack SaaS-style application that allows users to:

- Upload resumes
- Match them against job descriptions
- Receive structured AI feedback
- View analysis history
- Retry failed analyses
- Securely authenticate using JWT

This project demonstrates production-grade backend architecture, secure authentication, cloud deployment, and AI integration.

---

## 🧠 Features

### 🔐 Authentication & Security

- JWT-based stateless authentication
- Role-based access (USER / ADMIN)
- Secure password hashing (BCrypt)
- CORS configured for production (Vercel + Localhost)
- Rate limiting support
- Global exception handling

### 📄 CV Management

- Upload CV (Multipart)
- Download CV (Owner only)
- Delete CV
- User-specific CV listing

### 🤖 AI Analysis

- OpenAI integration
- ATS-style keyword match scoring
- Section-wise evaluation
- Retry failed analyses
- Analysis history tracking

### ☁️ Deployment

- Backend hosted on Railway
- PostgreSQL hosted on Railway
- Frontend hosted on Vercel
- Environment-based configuration (dev/prod profiles)

---

## 🏗 System Architecture

```
User
   ↓
Next.js Frontend (Vercel)
   ↓ REST API
Spring Boot Backend (Railway)
   ↓
PostgreSQL Database (Railway)
   ↓
OpenAI API
```

---

## 🧩 Architecture Breakdown

### Frontend

- Next.js 16 (App Router)
- TypeScript
- Tailwind CSS
- Axios with JWT interceptors
- Reusable components (PasswordInput, ProtectedRoute)

### Backend

- Java 21
- Spring Boot 3
- Spring Security (JWT Filter)
- JPA / Hibernate
- PostgreSQL
- OpenAI integration
- Modular scoring engine
- Profile-based configuration (dev, prod, ai)

### Infrastructure

- Railway (Backend + Database)
- Vercel (Frontend)
- Stateless architecture
- Environment variable injection

---

## 📂 Project Structure

```
cvanalyser
├── frontend/
│   ├── app/
│   ├── components/
│   └── lib/
│
├── src/main/java/
│   ├── config/
│   ├── security/
│   ├── controller/
│   ├── service/
│   ├── cv/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── model/
│   │   ├── repository/
│   │   └── analysis/
│   │       ├── service/
│   │       └── scoring/
│
├── src/main/resources/
│   ├── application.properties
│   ├── application-dev.properties
│   └── application-prod.properties
│
├── pom.xml
└── README.md
```

---

## 🔌 API Endpoints

### Authentication

- `POST /auth/register`
- `POST /auth/login`

### CV Management

- `GET /api/cvs`
- `POST /api/cvs`
- `DELETE /api/cvs/{id}`
- `GET /api/cvs/{id}/download`

### Analysis

- `POST /api/cvs/{cvId}/analysis`
- `GET /api/analyses/{analysisId}`
- `GET /api/cvs/{cvId}/analysis/latest`
- `GET /api/cvs/{cvId}/analyses`
- `POST /api/analyses/{analysisId}/retry`

---

## 🔐 Security Design

- Stateless JWT authentication
- Custom JWT filter
- Role-based access control
- BCrypt password encryption
- CORS restricted to:
    - `http://localhost:3000`
    - `https://*.vercel.app`
- Input validation & centralized error handling

---

## ⚙️ Configuration Profiles

### Development

- Local PostgreSQL
- `ddl-auto=update`
- SQL logging enabled

### Production

- Railway PostgreSQL
- `ddl-auto=validate`
- Secure environment variable injection
- No schema auto-modification

---

## 🚀 Running Locally

### Backend

```bash
mvn spring-boot:run
```

Runs on:

```
http://localhost:8080
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Runs on:

```
http://localhost:3000
```

---

## 🌍 Deployment Flow

1. Push to `master`
2. Railway auto-deploys backend
3. Vercel auto-deploys frontend
4. Environment variables injected securely
5. Production-ready system

---

## 👨‍💻 Author

Siddharth Pattnaik