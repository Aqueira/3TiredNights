FROM eclipse-temurin:11-jdk-alpine
WORKDIR /app
COPY target/aggregator-*.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","app.jar"]
