# Phase 1: Build the application
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copy everything from current folder to /app inside the container
COPY . .

# ✅ Give executable permission to mvnw script
RUN chmod +x mvnw

# Build your Spring Boot project using Maven wrapper
RUN ./mvnw clean install

# Phase 2: Run the application
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the built JAR from the build phase
COPY --from=build /app/target/Banshi-0.0.1-SNAPSHOT.jar app.jar

# Expose port (optional, useful for local testing)
EXPOSE 8080

# Command to run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
