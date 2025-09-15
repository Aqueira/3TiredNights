FROM eclipse-temurin:11-jdk-alpine
WORKDIR /app
COPY target/second-shard-level-info-*.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","app.jar"]
