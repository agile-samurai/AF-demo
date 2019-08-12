#!/usr/bin/env bash
set -e

python3.6 tools/cli.py
AWS_ACCOUNT_ID="$(bash tools/get-account-id.sh)"

cd scripts
pwd


export TF_VAR_bucket_name_training=mdas-training-demo-${AWS_ACCOUNT_ID}
export TF_VAR_bucket_name_processed_data=mdas-training-process-${AWS_ACCOUNT_ID}
export TF_VAR_bucket_name_state=mdas-state-demo-${AWS_ACCOUNT_ID}
export TF_VAR_bucket_aws_region=us-east-1
export TF_VAR_bucket_region=${TF_VAR_bucket_aws_region}
export TF_VAR_aws_region=${TF_VAR_bucket_aws_region}
export TF_VAR_bucket_name_ecr=mdas-tf-backend-ecr-${AWS_ACCOUNT_ID}

export TF_VAR_concourse_username=$GITHUB_USERNAME
export TF_VAR_concourse_password=$GITHUB_PASSWORD

export TF_VAR_concourse_rds_username=$GITHUB_USERNAME
export TF_VAR_concourse_rds_password=$GITHUB_PASSWORD

export TF_VAR_sonarqube_rds_password=$GITHUB_PASSWORD
export TF_VAR_sonarqube_rds_username=$GITHUB_USERNAME

export TF_VAR_environment=infra
export TF_VAR_project=infra
export TF_VAR_project_domain=infra.test

export TF_VAR_bucket_name=${TF_VAR_bucket_name_state}

echo $TF_VAR_bucket_name_training
#
cd data-setup
terraform init
terraform apply --auto-approve
cd ../

terraform init
terraform apply --auto-approve

export CONCOURSE_URL=`cat file.txt`
#
echo $CONCOURSE_URL

sleep 80
bash ../tools/wait-for-concourse.sh http://${CONCOURSE_URL} 200

fly -t demo login -c http://${CONCOURSE_URL} --username ${TF_VAR_concourse_rds_username} --password ${TF_VAR_concourse_password}
fly -t demo sync
fly -t demo login -c http://${CONCOURSE_URL} --username ${TF_VAR_concourse_rds_username} --password ${TF_VAR_concourse_password}
fly -t demo sp -c pipeline.yml -p mdas -l credentials.yaml -n
fly -t demo up -p mdas




