#!/bin/bash

# Exit on error
set -e

echo "🚀 Starting Infrastructure (Redis, Zookeeper, Kafka) via Docker..."
docker-compose up -d redis zookeeper kafka

echo "⏳ Waiting for Kafka & Redis to be ready..."
sleep 5

# Array of all service modules
SERVICES=(
    "gateway-service"
    "auth-service"
    "user-service"
    "chat-service"
    "message-service"
    "media-service"
    "presence-service"
    "notification-service"
    "analytics-service"
    "search-service"
)

# Trap SIGINT (Ctrl+C) to stop all background processes
trap 'kill $(jobs -p) 2>/dev/null || true' EXIT

echo "⚙️  Starting all microservices in parallel..."
for service in "${SERVICES[@]}"; do
    echo "▶️ Starting $service..."
    ./gradlew :$service:bootRun > logs_${service}.log 2>&1 &
done

echo "✅ All services are starting up! Logs are being written to logs_<service-name>.log"
echo "👉 Press Ctrl+C to stop all services."

# Wait for all background jobs to finish
wait
