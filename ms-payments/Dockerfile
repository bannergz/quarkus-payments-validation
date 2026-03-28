# Multi-stage build — ms-payments (Quarkus JVM)

# ============================================================
# Stage 1: Build
# ============================================================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Cache busting arg to force rebuild on each docker compose build
ARG FORCE_REBUILD=unknown

# Copy POM file first for layer caching of Maven dependencies
COPY pom.xml .
COPY checkstyle.xml .
RUN mvn dependency:go-offline -q

# Copy source and build the Quarkus fast-jar
COPY src ./src
RUN mvn package -DskipTests -q

# ============================================================
# Stage 2: Runtime (JVM mode — smaller than native for CI)
# ============================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl

# Quarkus fast-jar structure
COPY --from=builder /app/target/quarkus-app/lib/ ./lib/
COPY --from=builder /app/target/quarkus-app/*.jar ./
COPY --from=builder /app/target/quarkus-app/app/ ./app/
COPY --from=builder /app/target/quarkus-app/quarkus/ ./quarkus/

# Quarkus reads config/application.properties at runtime with highest priority
# Changing this file only rebuilds this final layer, not the full Maven build
COPY src/main/resources/application.properties ./config/application.properties

EXPOSE 8080

# Run as non-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]
