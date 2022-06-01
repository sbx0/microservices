FROM eclipse-temurin:17

MAINTAINER sbx0

HEALTHCHECK --interval=5s --timeout=30s CMD curl -f http://localhost:$GATEWAY_PORT/actuator/health || exit 1

ADD /*.jar /bootstrap.jar

# -Dreactor.netty.http.server.accessLogEnabled=true
ENTRYPOINT ["sh", "-c", "java -jar /bootstrap.jar --spring.profiles.active=$GATEWAY_PROFILES" ]

EXPOSE $GATEWAY_PORT
