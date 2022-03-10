FROM openjdk:8

MAINTAINER sbx0

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="" \
    PORT=8080 \
    REGISTRY_HOST="192.168.1.254" \
    REGISTRY_PORT=8761 \
    CONFIG_HOST="192.168.1.254" \
    CONFIG_PORT=8888 \
    REDIS_HOST="192.168.1.254" \
    REDIS_PASSWORD="test" \
    REDIS_PORT=6379 \
    PROFILES="dev" \
    REGION="virtual-machine" \
    VERSION="0.0.1"

ADD /*.jar /bootstrap.jar

RUN apt update && apt install curl

HEALTHCHECK --interval=5s --timeout=30s CMD curl -f http://localhost:$PORT/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dreactor.netty.http.server.accessLogEnabled=true -jar /bootstrap.jar --spring.profiles.active=$PROFILES" ]

EXPOSE $PORT
