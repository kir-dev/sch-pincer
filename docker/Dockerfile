FROM eclipse-temurin:17-jdk-alpine AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
COPY .mvn /usr/src/app/.mvn
COPY mvnw /usr/src/app
WORKDIR /usr/src/app
RUN ./mvnw clean package

FROM eclipse-temurin:17-jre-alpine
MAINTAINER gerviba@sch.bme.hu
COPY --from=build /usr/src/app/target/schpincer.jar /opt/schpincer/
COPY docker/application-docker.properties /opt/schpincer/application.properties
WORKDIR /opt/schpincer
ENTRYPOINT ["java", "-Dspring.profiles.include=docker", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseContainerSupport", "-XX:+UseSerialGC", "-XX:MaxRAMPercentage=90", "-jar", "/opt/schpincer/schpincer.jar"]
EXPOSE 80
