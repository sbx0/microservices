FROM eclipse-temurin:17

MAINTAINER sbx0

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.9.0/wait /wait
RUN chmod +x /wait

ADD /*.jar /bootstrap.jar

HEALTHCHECK --interval=5s --timeout=30s CMD curl -f http://localhost:$CONFIGURATION_PORT/actuator/health || exit 1

CMD /wait && java -jar /bootstrap.jar --spring.profiles.active=$CONFIGURATION_PROFILES

EXPOSE $CONFIGURATION_PORT
