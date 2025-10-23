FROM eclipse-temurin:24-jdk-alpine AS build
WORKDIR /usr/src/app

COPY pom.xml .
COPY .mvn ./.mvn
COPY mvnw .

RUN ./mvnw dependency:go-offline -B -U

COPY src ./src
RUN ./mvnw package -B

FROM eclipse-temurin:24-jre-alpine
MAINTAINER gerviba@sch.bme.hu

COPY --from=build /usr/src/app/target/schpincer.jar /opt/schpincer/

WORKDIR /opt/schpincer
ENTRYPOINT ["java", "-Dspring.profiles.include=env,production", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseContainerSupport", "-XX:+UseSerialGC", "-XX:MaxRAMPercentage=90", "-jar", "/opt/schpincer/schpincer.jar"]
EXPOSE 80
