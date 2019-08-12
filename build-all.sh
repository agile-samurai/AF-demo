./tools/prepare-ds-local.sh
mvn -f ./ui/server/pom.xml clean install -DskipTests -Pdocker
cd ui/sector-ui/
npm run build
cd container
./build-sector-ui.sh
cd ../../
