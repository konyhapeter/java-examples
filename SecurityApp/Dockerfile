FROM eclipse-temurin:25-jre

WORKDIR /app
COPY SecurityApp/target/SecurityApp-*.jar app.jar

EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]
