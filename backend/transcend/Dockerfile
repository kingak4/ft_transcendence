FROM gradle:8.5-jdk21 AS builder
WORKDIR /gradle

COPY gradle/ ./gradle
COPY transcend/build.gradle.kts ./
COPY settings.gradle.kts ./
COPY transcend/src ./src

RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine-3.23
WORKDIR /backend

COPY --from=builder /gradle/build/libs/backend-unspecified.jar app.jar

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "app.jar"]