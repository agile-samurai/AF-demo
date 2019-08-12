#!/usr/bin/env bash

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

export TF_VAR_bucket_name=$TF_VAR_bucket_name_state


terraform init
terraform refresh
terraform destroy -force

cd data-setup

terraform init
terraform refresh
terraform destroy -force


