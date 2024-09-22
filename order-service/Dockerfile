FROM openjdk:21-jdk-slim
WORKDIR /app
COPY ./target/order-service.jar /app
EXPOSE 8081
CMD ["java", "-jar", "order-service.jar"]