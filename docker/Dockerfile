FROM openjdk:8-jdk-alpine
MAINTAINER gerviba@sch.bme.hu
COPY schpincer-latest.jar /opt/schpincer/
WORKDIR /opt/schpincer
ENTRYPOINT ["java", "-jar", "/opt/schpincer/schpincer-latest.jar"]
EXPOSE 80
