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

# Install entr (file-watcher) and dos2unix in a single layer
RUN apt-get update && apt-get install -y entr dos2unix && rm -rf /var/lib/apt/lists/*

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY . .

# Convert the autocompile script to Unix format and make it executable
RUN dos2unix springAutoCompile.sh
RUN chmod +x springAutoCompile.sh

EXPOSE 8080
ENTRYPOINT ["./springAutoCompile.sh"]

# Production Stage
FROM gcr.io/distroless/java21:nonroot
ENV SPRING_PROFILES_ACTIVE=prod

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
CMD ["app.jar"]