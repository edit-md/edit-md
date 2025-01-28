#!/bin/bash

# Run Spring Boot application in an endless loop and watch for changes in the src directory
while true
do
  find /app/src | entr -n -r -d mvn spring-boot:run -Dspring-boot.run.fork=false
done