FROM openjdk:10
ADD target/file-upload-demo-0.0.1-SNAPSHOT.jar file-upload-demo-0.0.1-SNAPSHOT.jar
EXPOSE 8314
ENTRYPOINT ["java", "-jar", "file-upload-demo-0.0.1-SNAPSHOT.jar"]
