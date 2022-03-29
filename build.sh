#!/bin/bash
# sed -i 's/\r//' build.sh
# chmod u+x build.sh
# BUILD is where docker-compose build
BUILD=build
# SERVICES is all service
SERVICES=(
  microservices-account
  microservices-configuration
  microservices-gateway
  microservices-registry
  microservices-uno
)
SOURCE_CONFIGS="$PWD/microservices-configuration/src/main/resources/configurations/*"

init() {
  echo "create .env"
  echo "PASSWORD=test" >.env
  sed -i "\$a CONFIG_LOCATION=${PWD}/${BUILD}/configurations" .env
  sed -i '$a CONFIG_URL=127.0.0.1:8888' .env
  sed -i '$a REGISTRY_URL=http://127.0.0.1:8761/eureka/' .env
  sed -i '$a REDIS_HOST=127.0.0.1' .env
  sed -i '$a REDIS_PASSWORD=test' .env
  # echo "config environment"
  # sed -i "\$a MICROSERVICES_DIR=${PWD}" /etc/environment
  # source /etc/environment
  echo "init finished"
  mkdir "$PWD/$BUILD/configurations"
}

build() {
  echo "maven building"
  mvn -T 1C -DskipTests clean install
  echo "build finished"
}

copy() {
  for SERVICE in "${SERVICES[@]}"; do
    rm -rf "${PWD:?}/${BUILD:?}/${SERVICE:?}"
    mkdir "${PWD:?}/${BUILD:?}/${SERVICE:?}"
    cp -f ${PWD:?}"/"${SERVICE:?}"/"target/*.jar "${PWD:?}/${BUILD:?}/${SERVICE:?}"
    cp -f ${PWD:?}"/"${SERVICE:?}"/"*Dockerfile "${PWD:?}/${BUILD:?}/${SERVICE:?}"
    echo "${SERVICE} files copy"
  done

  cp -f $SOURCE_CONFIGS "${PWD:?}/${BUILD:?}/configurations"
  echo "configurations files copy"

  cp -f $PWD/docker-compose*.yml "${PWD:?}/${BUILD:?}"
  echo "docker-compose files copy"
  cp -f $PWD/compose.sh "${PWD:?}/${BUILD:?}"
  echo "compose.sh copy"
  cp -f $PWD/.env "${PWD:?}/${BUILD:?}"
  echo ".env copy"

  echo "copy finished"
}

case "$1" in
"init" | "i")
  init
  ;;
"build" | "b")
  build
  ;;
"copy" | "c")
  copy
  ;;
*)
  build
  ;;
esac
