FROM openjdk:8-jdk-alpine
MAINTAINER gerviba@sch.bme.hu
COPY webschop-latest.jar /opt/webschop/
WORKDIR /opt/webschop
ENTRYPOINT ["java", "-jar", "/opt/webschop/webschop-latest.jar"]
VOLUME /var/lib/spring-cloud/config-repo
EXPOSE 8080