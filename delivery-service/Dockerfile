FROM openjdk:21-jdk-slim
WORKDIR /app
COPY ./target/delivery-service.jar /app
EXPOSE 8084
CMD ["java", "-jar", "delivery-service.jar"]