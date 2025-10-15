FROM openjdk:21-jdk
WORKDIR /app
COPY target/Employee_Management_System-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
