# microservices [under construction]

This is a java microservice project built by Spring Cloud Framework

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
2. run `./build.sh i` to init (windows can use git bash to run)
2. run `./build.sh` to build
2. run `./build.sh c` to copy file into `build`
3. cd `build` folder
4. run `./compose.sh bp` to build and up
6. visit http://127.0.0.1:8761 to see services

# IDEA

`CONFIG_URL=http://127.0.0.1:8888;SECURITY_NAME=sbx0;SECURITY_PASSWORD=test;REGISTRY_URL=http://127.0.0.1:8761/eureka/`