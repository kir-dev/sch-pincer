FROM adoptopenjdk/openjdk9:x86_64-alpine-jdk-9.0.4.11 AS build  
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app  
COPY .mvn /usr/src/app/.mvn  
COPY mvnw /usr/src/app/  
RUN cd /usr/src/app && ./mvnw -f pom.xml clean package

FROM openjdk:8-jdk-alpine
MAINTAINER gerviba@sch.bme.hu
COPY --from=build /usr/src/app/target/schpincer.jar /opt/schpincer/
WORKDIR /opt/schpincer
ENTRYPOINT ["java", "-jar", "/opt/schpincer/schpincer.jar"]
EXPOSE 80
