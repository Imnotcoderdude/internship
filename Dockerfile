FROM openjdk:17.0.1-jdk-slim
VOLUME /tmp
COPY build/libs/intership-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar","/app.jar"]