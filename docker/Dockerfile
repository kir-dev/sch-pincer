FROM eclipse-temurin:17.0.1_12-jre-alpine AS build
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app  
COPY .mvn /usr/src/app/.mvn  
COPY mvnw /usr/src/app/  
RUN cd /usr/src/app && ./mvnw -f pom.xml clean package

FROM eclipse-temurin:17.0.1_12-jre-alpine
MAINTAINER gerviba@sch.bme.hu
COPY --from=build /usr/src/app/target/schpincer.jar /opt/schpincer/
COPY docker/application-docker.properties /opt/schpincer/application.properties
WORKDIR /opt/schpincer
ENTRYPOINT ["java", "-Dspring.profiles.include=docker", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseContainerSupport", "-XX:+UseSerialGC", "-XX:MaxRAMPercentage=90", "-jar", "/opt/schpincer/schpincer.jar"]
EXPOSE 80
