# Use OpenJDK 24 slim base image
FROM openjdk:24-jdk-slim

# Add jar to container
COPY target/*.jar app.jar

# Expose port (Spring Boot default)
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]