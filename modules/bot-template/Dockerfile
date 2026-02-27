FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY ./build/libs/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]