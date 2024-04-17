FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar stats/stats-server.jar
ENTRYPOINT ["java", "-jar", "stats/stats-server.jar"]