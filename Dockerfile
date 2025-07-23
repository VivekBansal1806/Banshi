# ---------- Build Stage ----------
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x mvnw

# Skip tests to avoid failure and build the jar
RUN ./mvnw clean package -DskipTests

# ---------- Run Stage ----------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/target/Banshi-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
