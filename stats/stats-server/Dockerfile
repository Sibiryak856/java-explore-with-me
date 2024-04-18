FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar stats-server.jar
ENV TZ="EUROPE/Moscow"
ENTRYPOINT ["java", "-jar", "/stats-server.jar"]