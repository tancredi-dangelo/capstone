# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# 1. Copy pom.xml and pre-fetch dependencies (cached unless pom.xml changes)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copy source code, package application, and isolate the executable JAR
COPY src ./src
RUN mvn clean package -DskipTests -B && \
    rm -f target/*plain.jar && \
    mv target/*.jar target/app.jar

# Stage 2: Runtime stage
FROM eclipse-temurin:25
WORKDIR /app
COPY --from=build /app/target/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]