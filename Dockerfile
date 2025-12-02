# FROM openjdk:11-jre-slim
FROM eclipse-temurin:11-jre-noble
LABEL Name=New_Site_Api

RUN apt-get update \
  && apt-get install -y vim \
  && apt-get install -y less \
  && apt-get install -y nano \
  && apt-get install -y curl \  
  && apt-get clean

RUN groupadd --gid 20000 spring && useradd --uid 20000 --no-log-init -M -g spring spring
USER spring:spring

COPY target/siteapp.jar siteservice.jar
EXPOSE 8080

HEALTHCHECK --start-period=30s CMD curl -s http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-jar","/siteservice.jar"]
