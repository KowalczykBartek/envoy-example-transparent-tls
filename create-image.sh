#!/bin/sh -u

# terminate script as soon as any command fails
set -e
SERVICE_NAME="ping-pong-server"
SETUP_NAME="setup"

./gradlew clean shadowJar

docker build -f ./setup/Dockerfile -t $SETUP_NAME  .
docker build -t $SERVICE_NAME  .