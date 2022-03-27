#!/bin/bash
# sed -i 's/\r//' build.sh
# chmod u+x build.sh
SERVICES=(
  microservices-account
  microservices-configuration
  microservices-gateway
  microservices-registry
  microservices-uno
)

init() {
  echo "create .env"
  echo -e "PASSWORD=test\nCONFIG_LOCATION=127.0.0.1:8888" >.env
  # echo "config environment"
  # sed -i "\$a MICROSERVICES_DIR=${PWD}" /etc/environment
  # source /etc/environment
  echo "init finished"
}

build() {
  echo "maven building"
  mvn -T 1C -DskipTests clean install
  echo "build finished"
  mkdir target
}

copy() {
  for SERVICE in "${SERVICES[@]}"; do
    rm -rf "$PWD"/target/"$SERVICE"
    mkdir "$PWD"/target/"$SERVICE"
    cp "$PWD"/"$SERVICE"/target/*.jar "$PWD"/target/"$SERVICE"
    cp "$PWD"/"$SERVICE"/*Dockerfile "$PWD"/target/"$SERVICE"
    echo "$SERVICE files copy"
  done

  rm -rf "$PWD"/target/configurations
  mkdir "$PWD"/target/configurations
  cp "$PWD"/microservices-configuration/src/main/resources/configurations/* "$PWD"/target/configurations
  echo "configurations files copy"

  cp "$PWD"/docker-compose*.yml "$PWD"/target
  echo "docker-compose files copy"
  cp "$PWD"/compose.sh "$PWD"/target
  echo "compose.sh copy"
  cp "$PWD"/.env "$PWD"/target
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
