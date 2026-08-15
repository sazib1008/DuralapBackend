# Multi-stage Dockerfile for Duralap Modular Monolith
# Supports ARM64 (Apple Silicon) and AMD64 (Intel/Windows) via eclipse-temurin:21

# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

RUN apk add --no-cache bash

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

COPY shared shared
COPY modules modules
COPY app app

ENV GRADLE_OPTS="-Dorg.gradle.jvmargs=-Xmx1024m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError"
RUN chmod +x gradlew && ./gradlew :app:bootJar -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl \
    && addgroup -S spring \
    && adduser -S spring -G spring

USER spring:spring

COPY --from=builder /app/app/build/libs/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xmx768m -Xms256m -XX:+UseG1GC"
ENV SPRING_PROFILES_ACTIVE="docker"

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
