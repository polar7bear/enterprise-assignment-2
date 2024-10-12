FROM gradle:jdk-21-and-22 AS build
WORKDIR /home/app

COPY ./build.gradle /home/app/build.gradle
COPY ./src/main/java/com/ssg/assignment2/Assignment2Application.java /home/app/src/main/java/com/ssg/assignment2/Assignment2Application.java

COPY . /home/app
RUN gradle build --no-daemon

FROM openjdk:24-slim-bullseye
EXPOSE 8080
COPY --from=build /home/app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]