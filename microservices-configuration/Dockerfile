FROM openjdk:8

MAINTAINER sbx0

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="" \
    PORT=8888 \
    REGISTRY_HOST="192.168.1.254" \
    REGISTRY_PORT=8761 \
    CONFIG_HOST="192.168.1.254" \
    CONFIG_PORT=8888 \
    PROFILES="native" \
    REGION="virtual-machine" \
    VERSION="0.0.1" \
    TZ=Asia/Shanghai

ADD /*.jar /bootstrap.jar

RUN apt update && apt install curl && apt install tzdata

HEALTHCHECK --interval=5s --timeout=30s CMD curl -f http://localhost:$PORT/actuator/health || exit 1

ADD /*.jar /bootstrap.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /bootstrap.jar --spring.profiles.active=$PROFILES"]

EXPOSE $PORT
