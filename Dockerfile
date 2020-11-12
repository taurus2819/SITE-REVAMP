FROM openjdk:11 as builder
COPY . .
ARG jenkins_auth
ARG artifactory_user
RUN ./mvnw spring-boot:build-info verify -s settings.xml -q

FROM openjdk:11-jre-slim
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

RUN addgroup --system spring && useradd -r -g spring spring
USER spring:spring

COPY --from=builder target/*.jar siteservice.jar
EXPOSE 8080

HEALTHCHECK --start-period=30s CMD curl -s http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-jar","/siteservice.jar"]