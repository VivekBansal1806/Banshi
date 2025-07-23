# Phase 1: Build the application
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x mvnw

# ✅ Skip tests with -DskipTests
RUN ./mvnw clean install -DskipTests

# Phase 2: Run the application
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/target/Banshi-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
