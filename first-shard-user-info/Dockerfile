# Используем легкий JDK
FROM eclipse-temurin:11-jdk-alpine

# Рабочая папка внутри контейнера
WORKDIR /app

# Копируем готовый JAR
COPY target/first-shard-user-info-*.jar app.jar

# Запуск приложения
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","app.jar"]
