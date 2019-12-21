FROM maven:3.6.3-jdk-8-openj9 AS build  
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app  
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:8-jdk-alpine
MAINTAINER gerviba@sch.bme.hu
COPY --from=build /usr/src/app/target/schpincer.jar /opt/schpincer/
WORKDIR /opt/schpincer
ENTRYPOINT ["java", "-jar", "/opt/schpincer/schpincer.jar"]
EXPOSE 80
