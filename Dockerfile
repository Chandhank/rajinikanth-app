FROM openjdk:17
COPY target/rajiniapp.jar /app/app.jar
COPY static /app/static
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

