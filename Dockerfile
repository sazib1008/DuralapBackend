FROM eclipse-temurin:21-jre-jammy

# Create a non-root user to run the application for security purposes
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Set the working directory inside the container
WORKDIR /app

# Argument specifying the JAR file built by Maven
ARG JAR_FILE=target/*.jar

# Copy the built fat-jar into the container
COPY ${JAR_FILE} app.jar

# Expose the port the Spring Boot application runs on
EXPOSE 8080

# Environment variables for overriding properties at runtime
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE="prod"

# Execute the application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]
