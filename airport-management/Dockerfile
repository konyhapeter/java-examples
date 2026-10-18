FROM maven:3.9.11-eclipse-temurin-25-alpine AS build
WORKDIR /workspace
COPY pom.xml .
COPY airport-management/pom.xml airport-management/pom.xml
COPY airport-management/src airport-management/src
RUN mvn -B -f airport-management/pom.xml package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /workspace/airport-management/target/airport-management-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
