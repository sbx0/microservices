FROM openjdk:8

MAINTAINER sbx0

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="" \
    NAME="sbx0" \
    PASSWORD="123456" \
    PORT=8080 \
    REGISTRY_URL="http://192.168.1.254:8761/eureka/" \
    CONFIG_URL="192.168.1.254:8888" \
    REDIS_HOST="192.168.1.254" \
    REDIS_PASSWORD="test" \
    REDIS_PORT=6379 \
    PROFILES="dev" \
    REGION="virtual-machine" \
    VERSION="0.0.1" \
    TZ=Asia/Shanghai

ADD /*.jar /bootstrap.jar

RUN apt update && apt install curl && apt install tzdata

HEALTHCHECK --interval=5s --timeout=30s CMD curl -f http://localhost:$PORT/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dreactor.netty.http.server.accessLogEnabled=true -jar /bootstrap.jar --spring.profiles.active=$PROFILES" ]

EXPOSE $PORT
