docker rmi records/frontend || true
cd ../
npm install
npm run build
cd ./container
cp -Rf ../build .
docker build  . -t records/frontend:latest
