#!/usr/bin/env bash


aws s3 cp s3://kmeans-smaller-model/model.tar.gz model.tar.gz
tar -xvzf model.tar.gz
rm -rf model.tar.gz

mv sector_clusterer.joblib services/scoring-service/extras/testing
mv ticker_df.joblib services/scoring-service/extras/testing
mv metadata.json services/scoring-service/extras/testing
mv nasdaq_sector_centroids.joblib services/scoring-service/extras/testing

