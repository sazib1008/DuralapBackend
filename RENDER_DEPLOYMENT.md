# Deploying Duralap Backend on Render

This guide walks you through deploying the **Duralap Modular Monolith Backend** (Spring Boot 3.4 + Kotlin + JVM 21) to [Render](https://render.com).

---

## 1. Prerequisites

Before deploying to Render, ensure you have:
1. **A MongoDB Instance / Cloud Database**:
   - Recommended: [MongoDB Atlas](https://www.mongodb.com/atlas) (Free M0 cluster or higher).
   - URI format: `mongodb+srv://<username>:<password>@<cluster>.mongodb.net/duralap?appName=Cluster0`
2. **A Redis Instance / Cloud Redis**:
   - Recommended: [Upstash Redis](https://upstash.com) (Free tier) or [Render Redis](https://render.com/docs/redis) or [Aiven Redis](https://aiven.io).
   - Non-TLS URI format: `redis://default:<password>@<host>:<port>`
   - TLS/SSL URI format: `rediss://default:<password>@<host>:<port>` (set `REDIS_SSL_ENABLED=true`)
3. **GitHub / GitLab Repository**:
   - Push your project code to your GitHub/GitLab repository.

---

## 2. Deployment Option A: Render Blueprint (1-Click Automated Setup)

The repository includes a ready-to-use [`render.yaml`](../render.yaml) Blueprint file.

1. Go to the [Render Dashboard](https://dashboard.render.com).
2. Click **New +** → **Blueprint**.
3. Connect your Git repository containing the Duralap project.
4. Render will detect `render.yaml` automatically.
5. Provide the required environment variables when prompted:
   - `MONGODB_URI`: Your MongoDB Atlas connection string.
   - `REDIS_URL`: Your Redis connection string.
   - `REDIS_SSL_ENABLED`: `true` (if using `rediss://`) or `false` (if standard `redis://`).
6. Click **Apply**. Render will build the Docker container and start your service.

---

## 3. Deployment Option B: Manual Web Service Setup

If you prefer setting up the Web Service manually via the Render UI:

1. In Render Dashboard, click **New +** → **Web Service**.
2. Connect your Git repository.
3. Configure the following fields:
   - **Name**: `duralapbackend` (or your chosen name)
   - **Region**: Choose the region closest to your MongoDB & Redis (e.g. `Oregon (US West)` or `Frankfurt (EU)`)
   - **Branch**: `main` (or your active branch)
   - **Root Directory**: `DuralapBackend`
   - **Runtime**: `Docker`
   - **Dockerfile Path**: `Dockerfile`
   - **Instance Type**: `Free` or `Starter`
4. Under **Advanced Settings**:
   - **Health Check Path**: `/actuator/health`
5. Under **Environment Variables**, add:

| Key | Example Value | Description |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `docker` | Activates Docker Spring profile |
| `PORT` | `8080` | Render assigns dynamic port, fallback 8080 |
| `MONGODB_URI` | `mongodb+srv://...` | Connection URI for MongoDB Atlas |
| `REDIS_URL` | `redis://default:password@host:6379` | Redis connection URL |
| `REDIS_SSL_ENABLED` | `false` (or `true` if `rediss://`) | SSL toggle for Redis |
| `JWT_SECRET` | `at-least-64-chars-long-secure-random-string-here` | Secret key for HS512 JWT |
| `JWT_EXPIRATION` | `86400000` | Access token lifespan (24 hours) |
| `JWT_REFRESH_EXPIRATION` | `604800000` | Refresh token lifespan (7 days) |
| `JAVA_OPTS` | `-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError` | JVM memory tuning |
| `SUPABASE_URL` | `https://xyz.supabase.co` | *(Optional)* Storage URL |
| `SUPABASE_BUCKET` | `media` | *(Optional)* Storage bucket |
| `SUPABASE_API_KEY` | `your-supabase-key` | *(Optional)* Supabase API key |
| `WEBRTC_TURN_URL` | `turn:turn.example.com:3478` | *(Optional)* WebRTC TURN URL |
| `WEBRTC_TURN_USERNAME` | `username` | *(Optional)* TURN Username |
| `WEBRTC_TURN_CREDENTIAL` | `password` | *(Optional)* TURN Password |

6. Click **Create Web Service**.

---

## 4. WebSocket & STOMP Support

Render natively supports WebSockets over HTTPS/WSS:
- **HTTP / REST URL**: `https://<your-app-name>.onrender.com/`
- **STOMP / WebSocket URL**: `wss://<your-app-name>.onrender.com/websocket`

> **Note**: On free tier instances, Render spins down inactive services after 15 minutes of inactivity. When a new connection arrives, it may take 30-50 seconds for cold start. Upgrading to a Starter instance prevents sleep.

---

## 5. Connecting Frontend (Android / Web)

Update your client configuration with your Render service URL:

- **REST API Base URL**: `https://<your-app-name>.onrender.com/`
- **WebSocket STOMP URL**: `wss://<your-app-name>.onrender.com/websocket`
