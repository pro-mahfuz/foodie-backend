FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src src
RUN mvn -q clean package -DskipTests

FROM eclipse-temurin:17-jre
RUN useradd --system --uid 10001 foodie
WORKDIR /app
COPY --from=build /workspace/target/foodie-backend-*.jar app.jar
USER foodie
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
