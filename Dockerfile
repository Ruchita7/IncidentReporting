FROM openjdk:8-jdk-alpine
COPY ./target/incidentreport-0.0.1-SNAPSHOT.jar incidentreport-0.0.1-SNAPSHOT.jar
CMD ["java","-jar","incidentreport-0.0.1-SNAPSHOT.jar"]