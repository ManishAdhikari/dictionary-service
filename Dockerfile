FROM eclipse-temurin:19-alpine@sha256:7e4fc4b3ae1bd8ed4205bfd76e4ebeefca3904653f8ee3ba6ac2427cc28b03c9

EXPOSE 8181 9191

COPY target/*.jar app.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "/app.jar"]