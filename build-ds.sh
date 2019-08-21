mkdir -p build/ds
cd build/ds
cp ../../services/data-science-service/rdso/* .
mkdir models
mkdir data
cd models
aws s3 cp s3://ugroup-rdso-challenge-data/models/metrics.0.0.0.json .
aws s3 cp s3://ugroup-rdso-challenge-data/models/movies_doc2vec.0.0.0.model .
aws s3 cp s3://ugroup-rdso-challenge-data/models/ . --recursive

cd ../data
aws s3 cp s3://ugroup-rdso-challenge-data/data/movies_df.pkl .
cd ..
docker build . -t ugroup/data-science-service
