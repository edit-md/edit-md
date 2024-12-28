#!/bin/bash

# Start Spring Boot application in the background
mvn spring-boot:run -Dspring-boot.run.fork=false &

# Watch for changes in the src directory and trigger mvn compile
find /app/src | entr -n -r mvn -T 1C compile