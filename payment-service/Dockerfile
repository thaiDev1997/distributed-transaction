FROM openjdk:21-jdk-slim
WORKDIR /app
COPY ./target/payment-service.jar /app
EXPOSE 8082
CMD ["java", "-jar", "payment-service.jar"]