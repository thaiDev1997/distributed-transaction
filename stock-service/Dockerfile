FROM openjdk:21-jdk-slim
WORKDIR /app
COPY ./target/stock-service.jar /app
EXPOSE 8083
CMD ["java", "-jar", "stock-service.jar"]