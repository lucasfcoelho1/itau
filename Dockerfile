FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/itau-app-api-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]