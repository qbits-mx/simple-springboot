FROM openjdk
COPY artemisa-0.0.1-SNAPSHOT.jar /
ENTRYPOINT ["java","-jar","artemisa-0.0.1-SNAPSHOT.jar"]

