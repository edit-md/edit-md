#!/bin/bash

# Run Spring Boot application in an endless loop in the background
while true; do
  mvn spring-boot:run -Dspring-boot.run.fork=false &
  wait $!
done &

# Watch for changes in the src directory and trigger mvn compile
find /app/src | entr -n -r mvn -T 1C compile