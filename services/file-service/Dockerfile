# Stage 1: Build Stage
FROM maven:3.9.5-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy the POM file and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the project and build
COPY src ./src
RUN mvn -T 1C clean package -DskipTests

# Stage 2: Runtime Stage
FROM gcr.io/distroless/java21:nonroot

# Set working directory
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application's port
EXPOSE 8080

# Run the application
CMD ["app.jar"]