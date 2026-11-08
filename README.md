# Modern Full-Stack Blog Platform

A production-ready, full-stack blog application built with **Spring Boot 4 (Java 25)** on the backend and **React 18 + TypeScript + Vite + NextUI** on the frontend, containerized with **Docker** and orchestrated using **Docker Compose**.

---

## 🌟 Features

- **Authentication & Security**:
  - Stateless JWT (JSON Web Token) authentication.
  - BCrypt password hashing.
  - Role-based authorization and protected routes.
  - Default administrative account initialized on first run.

- **Post Management**:
  - Full CRUD lifecycle for blog articles.
  - Draft vs. Published status support.
  - Dedicated "My Drafts" page for author drafts.
  - Rich text editor powered by **TipTap** (headings, bold, italics, ordered and unordered lists).
  - Automatic reading time calculation based on word count.
  - Fast filtering by category and interactive tag chips.

- **Taxonomy Management**:
  - Category creation, listing, updating, and deletion (with post count tracking).
  - Multi-tag batch creation, association, and deletion.
  - Prevention of accidental taxonomy deletion when posts are linked.

- **Responsive & Modern UI**:
  - Built with **NextUI** component library and **Tailwind CSS**.
  - Client-side routing with **React Router**.
  - Accessible, responsive design for desktop and mobile viewports.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Java 25, Spring Boot 4.1.2, Spring Data JPA, Spring Security, Hibernate ORM, MapStruct, PostgreSQL driver, JJWT |
| **Frontend** | React 18, TypeScript, Vite, NextUI, Tailwind CSS, TipTap Editor, Axios, Lucide Icons |
| **Database** | PostgreSQL 15 (Docker / Production), H2 in-memory (Unit & Integration tests) |
| **DevOps / Containers** | Docker, Docker Compose, Multi-stage builds, Nginx (Alpine) |

---

## 🚀 Quick Start with Docker Compose (Recommended)

The entire platform (PostgreSQL database, Spring Boot API, and Nginx-powered React Frontend) can be launched with a single command.

### Prerequisites
- [Docker](https://docs.docker.com/get-docker/) (v24+)
- [Docker Compose](https://docs.docker.com/compose/) (v2+)

### Steps

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd blog
   ```

2. **Configure environment variables (optional)**:
   ```bash
   cp .env.example .env
   ```

3. **Build and start all services**:
   ```bash
   docker compose up --build
   ```

4. **Access the application**:
   - **Frontend UI**: [http://localhost:5173](http://localhost:5173)
   - **Backend API**: [http://localhost:8080/api/v1](http://localhost:8080/api/v1)
   - **PostgreSQL**: `localhost:5432` (database: `blog`, user: `postgres`, password: `password`)

5. **Stop services**:
   ```bash
   docker compose down
   ```
   *(To remove volumes as well, run `docker compose down -v`)*

---

## 💻 Local Development Setup

If you prefer running services natively outside Docker:

### 1. Database Setup
Start a PostgreSQL instance locally or use the docker container:
```bash
docker run -d --name blog-postgres -p 5432:5432 -e POSTGRES_DB=blog -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=password postgres:15-alpine
```

### 2. Backend Setup
- **Requirements**: JDK 25 installed.
```bash
cd backend

# Run automated tests
./mvnw test

# Start the Spring Boot application (runs on http://localhost:8080)
./mvnw spring-boot:run
```

### 3. Frontend Setup
- **Requirements**: Node.js 20+ and npm installed.
```bash
cd frontend

# Install dependencies
npm install

# Start Vite development server with hot-reload (runs on http://localhost:5173)
npm run dev
```

---

## 🔐 Default Admin Credentials

Upon the initial backend launch, a default administrator account is automatically provisioned:

- **Email**: `admin@example.com`
- **Password**: `password`

> **Note**: For production environments, remember to change the default user password and update `JWT_SECRET` in `.env`.

---

## 📡 API Endpoints Overview

All backend endpoints are prefixed with `/api/v1`:

### Authentication
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/v1/auth/login` | Public | Authenticates user and returns JWT token |

### Posts
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/v1/posts` | Public | List published posts (supports `?categoryId=` and `?tagId=`) |
| `GET` | `/api/v1/posts/drafts` | Authenticated | List drafts created by the authenticated user |
| `GET` | `/api/v1/posts/{id}` | Public | Retrieve a single post by UUID |
| `POST` | `/api/v1/posts` | Authenticated | Create a new post (draft or published) |
| `PUT` | `/api/v1/posts/{id}` | Authenticated | Update an existing post (author only) |
| `DELETE` | `/api/v1/posts/{id}` | Authenticated | Delete a post (author only) |

### Categories
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/v1/categories` | Public | List all categories with published post count |
| `POST` | `/api/v1/categories` | Authenticated | Create a new category |
| `PUT` | `/api/v1/categories/{id}` | Authenticated | Update category name |
| `DELETE` | `/api/v1/categories/{id}` | Authenticated | Delete category (blocked if posts exist) |

### Tags
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/v1/tags` | Public | List all tags with published post count |
| `POST` | `/api/v1/tags` | Authenticated / Public | Batch create new tags |
| `DELETE` | `/api/v1/tags/{id}` | Authenticated | Delete tag (blocked if posts exist) |

---

## ⚙️ Environment Variables Reference

| Variable | Description | Default |
|---|---|---|
| `POSTGRES_DB` | PostgreSQL database name | `blog` |
| `POSTGRES_USER` | PostgreSQL user | `postgres` |
| `POSTGRES_PASSWORD` | PostgreSQL password | `password` |
| `POSTGRES_PORT` | PostgreSQL exposed host port | `5432` |
| `SPRING_DATASOURCE_URL` | JDBC connection URL | `jdbc:postgresql://postgres:5432/blog` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate schema management | `update` |
| `SPRING_JPA_SHOW_SQL` | Log SQL queries | `false` |
| `SECURITY_LOG_LEVEL` | Spring Security log level | `INFO` |
| `JWT_SECRET` | Base64-encoded secret key for JWT signing | *(Preconfigured default key for dev)* |
| `JWT_EXPIRATION` | Token validity in milliseconds | `86400000` (24 hours) |
| `FRONTEND_PORT` | Port for the web client | `5173` |
| `BACKEND_PORT` | Port for the API server | `8080` |

---

## 🧪 Testing

To execute the test suite:

- **Backend tests** (Includes mock MVC tests, JPA queries, and security authorization checks with H2 database):
  ```bash
  cd backend
  ./mvnw test
  ```

- **Frontend build & type verification**:
  ```bash
  cd frontend
  npm run build
  ```

---

## 📄 License

This project is licensed under the MIT License.

