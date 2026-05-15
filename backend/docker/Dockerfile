FROM eclipse-temurin:21-jre-alpine-3.23
WORKDIR /backend

COPY transcend-unspecified.jar app.jar

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "app.jar"]