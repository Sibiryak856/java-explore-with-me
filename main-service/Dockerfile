FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar main-service.jar
ENV TZ="EUROPE/Moscow"
ENTRYPOINT ["java", "-jar", "/main-service.jar"]