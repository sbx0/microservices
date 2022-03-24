#!/bin/bash
# chmod -R 777 copy.sh

DIR="/home/microservices/source_code"

rm -rf $DIR/target
mkdir $DIR/target

mkdir $DIR/target/microservices-account
cp $DIR/microservices-account/target/*.jar $DIR/target/microservices-account
cp $DIR/microservices-account/*Dockerfile $DIR/target/microservices-account

mkdir $DIR/target/microservices-configuration
cp $DIR/microservices-configuration/target/*.jar $DIR/target/microservices-configuration
cp $DIR/microservices-configuration/*Dockerfile $DIR/target/microservices-configuration

mkdir $DIR/target/microservices-gateway
cp $DIR/microservices-gateway/target/*.jar $DIR/target/microservices-gateway
cp $DIR/microservices-gateway/*Dockerfile $DIR/target/microservices-gateway

mkdir $DIR/target/microservices-registry
cp $DIR/microservices-registry/target/*.jar $DIR/target/microservices-registry
cp $DIR/microservices-registry/*Dockerfile $DIR/target/microservices-registry

mkdir $DIR/target/microservices-uno
cp $DIR/microservices-uno/target/*.jar $DIR/target/microservices-uno
cp $DIR/microservices-uno/*Dockerfile $DIR/target/microservices-uno

cp $DIR/docker-compose*.yml $DIR/target
cp $DIR/compose.sh $DIR/target
# .env file need you to create, it contain value you needs env
cp $DIR/.env $DIR/target
mkdir $DIR/target/configurations
cp $DIR/microservices-configuration/src/main/resources/configurations/* $DIR/target/configurations

echo "copy finished"
