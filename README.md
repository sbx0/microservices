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

1. run `build.bat`
2. copy `target` folder to your server
3. run `docker-compose build`
4. run `docker-compose up -d`
