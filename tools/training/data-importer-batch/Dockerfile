#FROM openjdk:10.0.2-13-jre
FROM openjdk:11.0.3-jdk-slim-stretch

COPY target/data-importer-batch-1.0-SNAPSHOT.jar /usr/app/

WORKDIR /usr/app/

EXPOSE 8080
EXPOSE 5000

ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar data-importer-batch-1.0-SNAPSHOT.jar