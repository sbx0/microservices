# microservices [under construction]

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://github.com/sbx0/microservices/actions/workflows/maven.yml/badge.svg)](https://github.com/sbx0/microservices/actions/workflows/maven.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/3de16d0630364cb9b3fe7a43a98fe6ba)](https://www.codacy.com/gh/sbx0/microservices/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=sbx0/microservices&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/3de16d0630364cb9b3fe7a43a98fe6ba)](https://www.codacy.com/gh/sbx0/microservices/dashboard?utm_source=github.com&utm_medium=referral&utm_content=sbx0/microservices&utm_campaign=Badge_Coverage)

This is a java microservice project built by Spring Cloud Framework

# [supported by JetBrains](https://jb.gg/OpenSourceSupport)

![jetbrains](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg)

# Demo

![uno-game-1.gif](https://s2.loli.net/2022/04/21/v5w6YTgM3GLpziO.gif)

# Composition

|Role|Framework|
|----|----|
|Registration Center|Netflix Eureka|
|Configuration Center|Spring Cloud Config|
|Gateway|Spring Cloud Gateway|
|HTTP client|Feign|
|Database|MySQL|
|Cache|Redis|
|Code Generator|MyBatis Plus Generator|
|Authority Authentication|Sa-Token|

# Running Examples

1. git clone
2. run `./build.sh i` to init
3.
   - run `./build.sh` to build and run on server
   - run `./build.sh local` to build and run on wsl2
4. visit http://127.0.0.1:8761 to Eureka

# IDEA RUN CONFIG

`CONFIG_URL=http://wsl2.sbx0.cn:8888;SECURITY_PASSWORD=test;SECURITY_NAME=sbx0;REGION=docker;VERSION=0.0.0;REGISTRY_URL=http://wsl2.sbx0.cn:8761/eureka/;IP_ADDRESS=172.30.32.1;DB_URL=jdbc:mysql://wsl2.sbx0.cn:3306/assembler?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true`

# WSL2

`/etc/init.d/docker start`

# arthas

`docker exec -it container_id /bin/bash`

`curl -O https://arthas.aliyun.com/arthas-boot.jar`

`java -jar arthas-boot.jar`

`watch cn.sbx0.microservices.uno.service.impl.GameRoomServiceImpl message {params} -x 2`

# Code Coverage

`mvn clean verify`
