FROM eclipse-temurin:17

MAINTAINER sbx0

HEALTHCHECK --interval=5s --timeout=30s CMD curl -f http://localhost:$REGISTRY_PORT/actuator/health || exit 1

ADD /*.jar /bootstrap.jar

ENTRYPOINT ["sh", "-c", "java -jar /bootstrap.jar --spring.profiles.active=$REGISTRY_PROFILES"]

EXPOSE $REGISTRY_PORT
