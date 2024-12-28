# Build Stage
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only the pom.xml to leverage Docker cache for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build
COPY src ./src
RUN mvn -T 1C clean package -DskipTests

# Development Stage
FROM maven:3.9.5-eclipse-temurin-21 AS dev
ENV SPRING_PROFILES_ACTIVE=dev

WORKDIR /app

# Install entr (file-watcher) in a single layer
RUN apt-get update && apt-get install -y entr

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY springAutoCompile.sh .
RUN chmod +x springAutoCompile.sh

COPY . .

EXPOSE 8080
CMD ["sh", "./springAutoCompile.sh"]

# Production Stage
FROM gcr.io/distroless/java21:nonroot
ENV SPRING_PROFILES_ACTIVE=prod

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
CMD ["app.jar"]