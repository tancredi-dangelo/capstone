# Stage 1: Build stage (Java 21 LTS for faster build & runtime efficiency)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Resolve core project dependencies only (avoids downloading Doxia/site/docs plugins)
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:resolve -B

# 2. Copy source code and build, explicitly skipping tests, javadocs, and site generation
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn package \
    -DskipTests \
    -Dmaven.javadoc.skip=true \
    -Dmaven.site.skip=true \
    -Dmaven.source.skip=true \
    -B && \
    rm -f target/*plain.jar && \
    mv target/*.jar target/app.jar

# Stage 2: Runtime stage (Lightweight Alpine JRE image ~150MB instead of full JDK ~600MB)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Run as non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]