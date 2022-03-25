#!/bin/bash
# chmod -R 777 build.sh
mvn -T 1C -DskipTests clean install

echo "build finished"

DIR="/home/microservices/source_code"

$DIR/copy.sh

