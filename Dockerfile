FROM openjdk:17
COPY target/transaction-app-1.0.0.jar transaction-app-1.0.0.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "transaction-app-1.0.0.jar"]