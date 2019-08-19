mkdir -p build/ds
cd build/ds
cp ../../services/data-science-service/rdso/* .
mkdir models
mkdir data
cd models
aws s3 cp s3://rdso-challenge2/data/ci/models/metrics.0.0.0.json .
aws s3 cp s3://rdso-challenge2/models/movies_doc2vec.0.0.0.model .
aws s3 cp s3://rdso-challenge2/models/ . --recursive

cd ../data
aws s3 cp s3://rdso-challenge2/data/ci/data/movies_df.pkl .
cd ..
docker build . -t ugroup/data-science-service
