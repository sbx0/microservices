FROM eclipse-temurin:17

MAINTAINER sbx0

RUN apt update && apt -y install curl && apt -y install tzdata

HEALTHCHECK --interval=5s --timeout=30s CMD curl -f http://127.0.0.1:$GATEWAY_PORT/ACCOUNT/actuator/health || exit 1

ADD /*.jar /bootstrap.jar

ENTRYPOINT ["sh", "-c", "java -jar /bootstrap.jar --spring.profiles.active=$ACCOUNT_PROFILES"]
