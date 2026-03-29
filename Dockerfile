FROM gradle:8.14.4-jdk17 AS build
WORKDIR /workspace
COPY . .
RUN gradle :launcher:bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/launcher/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
