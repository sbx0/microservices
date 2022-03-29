#!/bin/bash
# chmod u+x compose.sh

SOURCE_CONFIGS="../microservices-configuration/src/main/resources/configurations"
CHANGED_CONFIGS="./configurations/*"

build() {
  docker-compose -f docker-compose-prod.yml --env-file .env build
}

up() {
  if [ -n "$1" ]; then
    docker-compose -f docker-compose-prod.yml --env-file .env up --compatibility -d "$1"
  else
    docker-compose -f docker-compose-prod.yml --env-file .env up --compatibility -d microservices-gateway
  fi
}

down() {
  docker-compose -f docker-compose-prod.yml --env-file .env down
}

build-and-up() {
  build
  up
}

restart() {
  if [ -n "$1" ]; then
    docker-compose -f docker-compose-prod.yml --env-file .env restart "$1"
  else
    echo "service-name require"
    echo "eg. bash compose.sh restart service-name"
  fi
}

log() {
  docker-compose logs -f --tail=1
}

config() {
  docker-compose -f docker-compose-prod.yml --env-file .env config
}

back() {
  cp -f $CHANGED_CONFIGS $SOURCE_CONFIGS
}

case "$1" in
"build" | "b")
  build
  ;;
"up" | "p")
  up "$2"
  ;;
"build-and-up" | "bp")
  build-and-up
  ;;
"restart" | "re")
  restart "$2"
  ;;
"config" | "c")
  config
  ;;
"back")
  back
  ;;
"log" | "l")
  log
  ;;
"down" | "d")
  down
  ;;
*)
  up "$2"
  ;;
esac
