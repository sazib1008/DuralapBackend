# Multi-stage Dockerfile for Duralap Spring Boot Microservices
# Supports ARM64 (Apple Silicon) and AMD64 (Intel/Windows) via eclipse-temurin multi-arch images

# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

RUN apk add --no-cache bash

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

COPY common common
COPY gateway-service gateway-service
COPY auth-service auth-service
COPY user-service user-service
COPY chat-service chat-service
COPY message-service message-service
COPY media-service media-service
COPY presence-service presence-service
COPY notification-service notification-service
COPY analytics-service analytics-service
COPY search-service search-service

ENV GRADLE_OPTS="-Dorg.gradle.jvmargs=-Xmx768m -XX:MaxMetaspaceSize=384m -XX:+HeapDumpOnOutOfMemoryError"
RUN chmod +x gradlew && ./gradlew \
    :gateway-service:bootJar \
    :auth-service:bootJar \
    :user-service:bootJar \
    :chat-service:bootJar \
    :message-service:bootJar \
    :media-service:bootJar \
    :presence-service:bootJar \
    :notification-service:bootJar \
    :analytics-service:bootJar \
    :search-service:bootJar \
    -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl \
    && addgroup -S spring \
    && adduser -S spring -G spring

USER spring:spring

ARG SERVICE_NAME=gateway-service
COPY --from=builder /app/${SERVICE_NAME}/build/libs/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE="docker"

HEALTHCHECK NONE

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
