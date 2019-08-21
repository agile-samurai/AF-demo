#!/bin/bash
set -e

echo "begin build front end script"
cd source/ui/frontend

echo "building front end"
npm install
yarn build
echo "front end build complete"

echo "pushing results to sonarqube"
echo "pushing results to sonarqube complete"

echo "preparing files for container build"
cp ./container/Dockerfile ../../../docker/.
cp ./container/start-nginx.sh ../../../docker/.
cp -Rf ./container/config ../../../docker/.
cp -Rf ./build ../../../docker/.
