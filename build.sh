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
WSL_IP=$(ifconfig eth0 | grep -w inet | awk '{print $2}')

init() {
  echo "create .env"
  echo "# HOST_CONFIG_LOCATION which is where your configurations files located" >.env
  sed -i '$a HOST_CONFIG_LOCATION=/mnt/c/Users/JsonSnow/IdeaProjects/microservices/build/configurations' .env
  sed -i '$a TZ=Asia/Shanghai' .env
  sed -i '$a REGION=wsl' .env
  sed -i '$a VERSION=0.0.0' .env
  sed -i '$a REGISTRY_URL=http://wsl2.sbx0.cn:8761/eureka/' .env
  sed -i '$a SECURITY_NAME=sbx0' .env
  sed -i '$a SECURITY_PASSWORD=test' .env
  sed -i '$a CONFIG_URL=http://wsl2.sbx0.cn:8888' .env
  sed -i '$a CONFIG_LOCATION=/home/sbx0/configurations' .env
  sed -i '$a REDIS_HOST=wsl2.sbx0.cn' .env
  sed -i '$a REDIS_PORT=6379' .env
  sed -i '$a REDIS_PASSWORD=test' .env
  sed -i '$a DB_URL=jdbc:mysql://wsl2.sbx0.cn:3306/assembler?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true' .env
  sed -i '$a DB_USERNAME=root' .env
  sed -i '$a DB_PASSWORD=test' .env
  sed -i '$a # REGISTRY' .env
  sed -i '$a REGISTRY_PORT=8761' .env
  sed -i '$a REGISTRY_PROFILES=dev' .env
  sed -i '$a # CONFIGURATION' .env
  sed -i '$a CONFIGURATION_PORT=8888' .env
  sed -i '$a CONFIGURATION_PROFILES=native' .env
  sed -i '$a # GATEWAY' .env
  sed -i '$a GATEWAY_PORT=8080' .env
  sed -i '$a GATEWAY_PROFILES=dev' .env
  sed -i '$a NEXT_URL=http://win.sbx0.cn:3000' .env
  sed -i '$a # ACCOUNT' .env
  sed -i '$a ACCOUNT_PORT=0' .env
  sed -i '$a ACCOUNT_PROFILES=dev' .env
  sed -i '$a # UNO' .env
  sed -i '$a UNO_PORT=0' .env
  sed -i '$a UNO_PROFILES=dev' .env
  sed -i "\$a IP_ADDRESS=${WSL_IP}" .env
  # sed -i "\$a CONFIG_LOCATION=${PWD}/${BUILD}/configurations" .env
  # echo "config environment"
  # sed -i "\$a MICROSERVICES_DIR=${PWD}" /etc/environment
  # source /etc/environment
  echo "init finished"
  mkdir "$PWD/$BUILD"
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

build-copy-up() {
  build
  copy
  cd build || exit
  ./compose.sh build-and-up "$1"
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
  build-copy-up "$1"
  ;;
esac
