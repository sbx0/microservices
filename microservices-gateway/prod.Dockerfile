FROM openjdk:8

MAINTAINER sbx0

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="" \
    NAME="sbx0" \
    PASSWORD="123456" \
    PORT=8080 \
    CONFIG_URL="127.0.0.1:8888" \
    PROFILES="prod" \
    REGION="aliyun" \
    VERSION="0.0.1" \
    TZ=Asia/Shanghai

ADD /*.jar /bootstrap.jar

RUN apt update && apt install curl && apt install tzdata

HEALTHCHECK --interval=5s --timeout=30s CMD curl -f http://localhost:$PORT/actuator/health || exit 1

ADD /*.jar /bootstrap.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dreactor.netty.http.server.accessLogEnabled=true -jar /bootstrap.jar --spring.profiles.active=$PROFILES"]

EXPOSE $PORT
