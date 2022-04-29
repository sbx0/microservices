FROM eclipse-temurin:17

MAINTAINER sbx0

ADD /*.jar /bootstrap.jar

RUN apt update && apt -y install curl && apt -y install tzdata

HEALTHCHECK --interval=5s --timeout=30s CMD curl -f http://wsl2.sbx0.cn:$GATEWAY_PORT/UNO/actuator/health || exit 1

ADD /*.jar /bootstrap.jar

ENTRYPOINT ["sh", "-c", "java -jar /bootstrap.jar --spring.profiles.active=$UNO_PROFILES"]
