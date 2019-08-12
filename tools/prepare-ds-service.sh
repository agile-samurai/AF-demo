#!/usr/bin/env bash


env=$1


if [[ $# -eq 0 ]]; then
    aws s3 cp s3://kmeans-run-training-version/version .
    version=`cat version`
    rm version

    echo "$version"
    canonical="${version//./-}"

    if [[ ${canonical} = "1-19-0" ]]; then
        bucket="sagemaker-us-east-1-971148336196"
        key="kmeans-run-training-${version}/output/model.tar.gz"
    else
        bucket="sagemaker-us-east-1-${AWS_ACCOUNT_ID}"
        key="kmeans-run-training-${version}/output/model.tar.gz"
    fi
    target="serialized"
else
    bucket="kmeans-smaller-model"
    key="model.tar.gz"
    target="testing"
fi


aws s3api get-object --bucket ${bucket} --key ${key} model.tar.gz
#aws s3 cp ${bucket} model.tar.gz
tar -xvzf model.tar.gz
rm -rf model.tar.gz

mv sector_clusterer.joblib source/services/scoring-service/extras/${target}
mv ticker_df.joblib source/services/scoring-service/extras/${target}
mv metadata.json source/services/scoring-service/extras/${target}
mv nasdaq_sector_centroids.joblib source/services/scoring-service/extras/${target}


cd ../../
