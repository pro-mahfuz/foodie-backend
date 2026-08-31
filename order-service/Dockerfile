FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY user-service/pom.xml user-service/pom.xml
COPY food-service/pom.xml food-service/pom.xml
COPY order-service/pom.xml order-service/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY config-server/pom.xml config-server/pom.xml
COPY order-service/src order-service/src
RUN mvn -B -pl order-service -am package -DskipTests
FROM eclipse-temurin:17-jre
COPY --from=build /app/order-service/target/order-service-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
