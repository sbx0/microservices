#!/bin/bash
# chmod u+x compose.sh

SOURCE_CONFIGS="../microservices-configuration/src/main/resources/configurations"
CHANGED_CONFIGS="./configurations/*"

up() {
    if [ -n "$1" ]; then
      docker-compose --compatibility up -d --build "$1"
    else
      docker-compose --compatibility up -d --build
    fi
}

restart() {
    if [ -n "$1" ]; then
      docker-compose --no-deps --build .env restart "$1"
    else
      echo "service-name require"
      echo "eg. bash compose.sh restart service-name"
    fi
}



log() {
    docker-compose logs -f --tail=1
}

config() {
    docker-compose -f docker-compose.yml config
}

back() {
  cp -f $CHANGED_CONFIGS $SOURCE_CONFIGS
}

stats() {
  docker stats
}

down() {
    docker-compose down
}

case "$1" in
"up")
  up "$2" "$3"
  ;;
"restart" | "re")
  restart "$2"
  ;;
"log")
  log
  ;;
"stats" | "st")
  stats
  ;;
"back")
  back
  ;;
"config")
  config "$2"
  ;;
"down")
  down "$2"
  ;;
*)
  stats
  ;;
esac
