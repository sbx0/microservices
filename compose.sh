#!/bin/bash
# chmod u+x compose.sh

SOURCE_CONFIGS="../microservices-configuration/src/main/resources/configurations"
CHANGED_CONFIGS="./configurations/*"

build() {
  case "$1" in
  "local")
    docker-compose -f docker-compose.yml --env-file .env build
    ;;
  *)
    docker-compose -f docker-compose-prod.yml --env-file .env build
    ;;
  esac
}

up() {
  # $1 environment eg. local / server
  # $2 service_name eg. microservices-registry / microservices-configuration
  case "$1" in
  "local")
    if [ -n "$2" ]; then
      docker-compose -f docker-compose.yml --env-file .env --compatibility up -d "$2"
    else
      docker-compose -f docker-compose.yml --env-file .env --compatibility up -d
    fi
    ;;
  *)
    if [ -n "$2" ]; then
      docker-compose -f docker-compose-prod.yml --env-file .env --compatibility up -d "$2"
    else
      docker-compose -f docker-compose-prod.yml --env-file .env --compatibility up -d
    fi
    ;;
  esac
}

down() {
  case "$1" in
  "local")
    docker-compose -f docker-compose.yml --env-file .env down
    ;;
  *)
    docker-compose -f docker-compose-prod.yml --env-file .env down
    ;;
  esac
}

build-and-up() {
  build "$1" "$2"
  up "$1" "$2"
}

restart() {
  case "$1" in
  "local")
    if [ -n "$2" ]; then
      docker-compose -f docker-compose.yml --env-file .env --no-deps --build restart "$2"
    else
      echo "service-name require"
      echo "eg. bash compose.sh restart service-name"
    fi
    ;;
  *)
    if [ -n "$2" ]; then
      docker-compose -f docker-compose-prod.yml --env-file --no-deps --build .env restart "$2"
    else
      echo "service-name require"
      echo "eg. bash compose.sh restart service-name"
    fi
    ;;
  esac
}

log() {
  case "$1" in
  "local")
    docker-compose -f docker-compose.yml --env-file .env logs -f --tail=1
    ;;
  *)
    docker-compose -f docker-compose-prod.yml --env-file logs -f --tail=1
    ;;
  esac
}

config() {
  case "$1" in
  "local")
    docker-compose -f docker-compose.yml --env-file .env config
    ;;
  *)
    docker-compose -f docker-compose-prod.yml --env-file .env config
    ;;
  esac
}

back() {
  cp -f $CHANGED_CONFIGS $SOURCE_CONFIGS
}

stats() {
  docker stats
}

case "$1" in
"build" | "b")
  build "$2"
  ;;
  # ./compose.sh up environment service_name
"up" | "p")
  up "$2" "$3"
  ;;
"build-and-up" | "bp")
  build-and-up "$2" "$3"
  ;;
"restart" | "re")
  restart "$2"
  ;;
"config" | "c")
  config "$2"
  ;;
"back")
  back
  ;;
"stats")
  stats
  ;;
"log" | "l")
  log "$2"
  ;;
"down" | "d")
  down "$2"
  ;;
*)
  stats
  ;;
esac
