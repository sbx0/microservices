#!/bin/bash
# chmod u+x compose.sh

build() {
  docker-compose -f docker-compose-prod.yml --env-file .env build
}

up() {
  docker-compose -f docker-compose-prod.yml --env-file .env up -d microservices-configuration
}

build-and-up() {
  build
  up
}

log() {
  docker-compose logs -f --tail=1
}

case "$1" in
"build" | "b")
  build
  ;;
"up" | "p")
  up
  ;;
"build-and-up" | "bp")
  build-and-up
  ;;
"log" | "l")
  log
  ;;
*)
 up
  ;;
esac
