FROM openjdk:17-alpine
WORKDIR /app/messaging
EXPOSE 8022
COPY target/messaging-0.0.1-SNAPSHOT.jar  messaging.jar
CMD java -Dspring.profiles.active=prod -jar messaging.jar