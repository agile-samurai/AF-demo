PROJECT_ROOT=$(pwd)
UI_SERVER_FRONTEND=$PROJECT_ROOT/frontend
UI_SERVER_BACKEND=$PROJECT_ROOT/server
DATA_SCIENCE_ROOT=$PROJECT_ROOT/services/data-science-service

# Build UI server
docker rmi records/ui-backend || true && cd ui/server && mvn clean install -Pdocker -DskipTests

# Build UI client
cd $UI_SERVER_FRONTEND/container && ./build-frontend.sh

# Build data science service (REST-based application)
cd $PROJECT_ROOT && ./build-ds.sh

# Build data science notebook
cd $DATA_SCIENCE_ROOT && docker rmi ugroup/data-science-notebook || true
docker build -t ugroup/data-science-notebook . --build-arg AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID --build-arg AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY
