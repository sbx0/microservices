#!/bin/bash
# chmod u+x compose.sh

build() {
  docker-compose -f docker-compose-prod.yml --env-file .env build
}

up() {
  if [ -n "$1" ]; then
    docker-compose -f docker-compose-prod.yml --env-file .env up -d "$1"
  else
    docker-compose -f docker-compose-prod.yml --env-file .env up -d microservices-gateway
  fi
}

down() {
  docker-compose -f docker-compose-prod.yml --env-file .env down
}

build-and-up() {
  build
  up
}

log() {
  docker-compose logs -f --tail=1
}

config() {
  docker-compose -f docker-compose-prod.yml --env-file .env config
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
"config" | "c")
  config
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
