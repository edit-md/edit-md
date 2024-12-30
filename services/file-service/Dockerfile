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
RUN apt-get update && apt-get install -y entr && rm -rf /var/lib/apt/lists/*

# Copy pom.xml and download dependencies
COPY pom.xml .

# Remove pom.xml after resolving dependencies
RUN mvn dependency:go-offline -B && rm pom.xml

# Copy auto compile script and source code
COPY springAutoCompile.sh .
RUN chmod +x springAutoCompile.sh
COPY . .

# Persistent Maven cache for development
VOLUME /root/.m2  
EXPOSE 8080
CMD ["sh", "./springAutoCompile.sh"]

# Production Stage
FROM gcr.io/distroless/java21:nonroot
ENV SPRING_PROFILES_ACTIVE=prod

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
CMD ["app.jar"]


