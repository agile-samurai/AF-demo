FROM openjdk:12

COPY target/ui-backend-1.0-SNAPSHOT.jar /usr/app/

WORKDIR /usr/app/

EXPOSE 8080
EXPOSE 5000

ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar ui-backend-1.0-SNAPSHOT.jar