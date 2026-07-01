FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src/ src/
RUN ./mvnw clean package -DskipTests -B -o

FROM eclipse-temurin:25-jre
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8092
HEALTHCHECK --interval=15s --timeout=5s --retries=5 --start-period=90s \
    CMD curl -sf http://localhost:8092/api/v1/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
