FROM openjdk:21

ARG JAR_FILE=target/*.jar

ADD ${JAR_FILE} api-service.jar

ENTRYPOINT ["java", "-jar", "api-service.jar"]

EXPOSE 2004
#EXPOSE 2004 -- Dev
#EXPOSE 8081 -- Test
#EXPOSE 8082 -- Prod