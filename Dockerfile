FROM eclipse-temurin:21

ADD https://dtdg.co/latest-java-tracer /dd-java-agent.jar

ARG JAR_FILE=build/libs/eatda-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} api.jar
