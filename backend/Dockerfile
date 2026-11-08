# Stage 1: Build
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app

# Copy Maven wrapper files and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Ensure maven wrapper is executable
RUN chmod +x ./mvnw

# Pre-fetch dependencies
RUN ./mvnw dependency:go-offline -B || true

# Copy source code and package the application
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:25-jre
WORKDIR /app

# Create a non-root user for security
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Copy built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

