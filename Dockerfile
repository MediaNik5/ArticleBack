FROM openjdk:17-alpine

VOLUME /tmp
# Copy the jar to the production image from the builder stage.
COPY target/*.jar executable.jar


# Run the web service on container startup.
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/executable.jar"]