#!/bin/bash
# chmod u+x compose.sh
docker-compose -f docker-compose-prod.yml --env-file .env up -d microservices-configuration
