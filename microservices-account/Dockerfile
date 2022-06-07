FROM eclipse-temurin:17

MAINTAINER sbx0

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.9.0/wait /wait
RUN chmod +x /wait

ADD /*.jar /bootstrap.jar

CMD /wait && java -jar /bootstrap.jar --spring.profiles.active=$ACCOUNT_PROFILES
