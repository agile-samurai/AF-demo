docker rmi records/sector-ui || true
cd ../
npm install
npm run build
cd ./container
cp -Rf ../build .
docker build  . -t records/sector-ui:latest
