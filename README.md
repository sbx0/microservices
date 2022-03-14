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
2. run `build.sh` or `build.bat`
3. cd `target` folder
4. run `docker-compose build`
5. run `docker-compose up -d`
6. visit http://127.0.0.1:8761 to see services