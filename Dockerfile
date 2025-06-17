FROM openjdk:17
COPY target/rajini-app-1.0-jar-with-dependencies.jar app.jar
COPY static/rajini.jpg /rajini.jpg
ENTRYPOINT ["java", "-jar", "app.jar"]

