# DevPath AI

**AI Career Intelligence Platform**

DevPath AI is a backend system that analyzes resumes, detects skills, predicts career paths, generates learning roadmaps, and automatically finds and applies to relevant jobs.

The system combines **AI analysis, knowledge graph reasoning, and job market data** to help developers improve their careers strategically.

---

# Features

## Resume Processing

* Upload resume (PDF)
* Automatic text extraction
* AI skill detection
* Resume skill database

## AI Career Analysis

* Best role prediction
* Skill gap detection
* Career insights

Example:

```
Best role: DevOps Engineer (78%)
Missing skills: Kubernetes, AWS
```

---

## Knowledge Graph Learning Roadmap

DevPath AI understands **skill dependencies**.

Example graph:

```
Linux → Docker → Kubernetes → DevOps
```

Learning roadmap generated automatically:

```
1. Linux
2. Docker
3. Kubernetes
```

---

## Job Recommendation Engine

Jobs are fetched using the **JSearch API**.

The system:

* Finds jobs matching the resume
* Calculates a **match score**
* Lists missing skills for each job

Example:

```
DevOps Engineer
Company: BAE Systems
Match Score: 82%
Missing Skills: Kubernetes
```

---

## Career Simulator

Simulate learning new skills and see how it improves career alignment.

Example:

```
Current DevOps score: 78%
If you learn: Kubernetes + AWS
Predicted score: 92%
```

---

## Auto Apply Engine

Automatically applies to jobs above a match threshold.

Example logic:

```
If matchScore ≥ 70
    Apply automatically
```

Applications are tracked in the system.

---

# Architecture

```
Frontend (React / Web)
        │
        ▼
Spring Boot REST API
        │
        ▼
AI Analysis Layer
        │
        ▼
Skill Knowledge Graph
        │
        ▼
PostgreSQL Database
```

Core components:

```
Resume Processing Engine
Skill Extraction Engine
AI Career Analyzer
Knowledge Graph Roadmap Generator
Job Recommendation Engine
Career Simulator
Auto Apply Engine
```

---

# 🗄 Database Schema

Tables:

```
users
resumes
analysis_result
skills
resume_skills
skill_dependency
job_applications
```

Relationships:

```
User
 └── Resumes
        └── ResumeSkills
               └── Skills
                      └── SkillDependencies
```

---

# API Endpoints

## Authentication

```
POST /api/auth/register
POST /api/auth/login
```

## Resume

```
POST /api/resumes/upload
GET /api/resumes
GET /api/resumes/{id}
DELETE /api/resumes/{id}
POST /api/resumes/{id}/reanalyze
```

## Career Analysis

```
GET /api/resumes/{resumeId}/roles/{role}
```

## Career Simulator

```
POST /api/career/simulate
```

## Jobs

```
GET /api/jobs/recommendations
POST /api/jobs/auto-apply
```

---

# Tech Stack

Backend:

```
Java 17
Spring Boot
Spring Security (JWT)
Hibernate / JPA
PostgreSQL
Apache PDFBox
```

APIs:

```
OpenAI
RapidAPI JSearch
```

Architecture:

```
REST API
Service Layer
Repository Layer
Knowledge Graph Layer
```

---

# Future Improvements

* AI Skill Knowledge Graph learning from job descriptions
* Resume improvement suggestions
* Job application dashboard
* Market demand analysis
* Skill importance weighting

---

# Author

Daniel Korik
Computer Science Graduate
Backend & AI Systems Developer
