FROM openjdk:17-jdk
EXPOSE 9090
COPY target/Employee_Management_System-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]


