docker rmi mdas/sector-ui || true
cd ../
npm install
npm run build
cd ./container
cp -Rf ../build .
docker build  . -t mdas/sector-ui:latest
