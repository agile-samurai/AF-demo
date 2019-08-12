#!/bin/bash

if [ "$#" -ne 6 ]; then
    echo "You must enter exactly 6 command line arguments"
    cat << EOF
    usage: $0 \
           <backend-bucket> \
           <project> \
           <environment> \
           <aws_region> \
           <aws_access_key_id> \
           <aws_secret_access_key> 
EOF
    exit 1;
fi

TF_BACKEND_BUCKET=$1
TF_PROJECT=$2
TF_ENVIRONMENT=$3
TF_AWS_REGION=$4
AWS_ACCESS_KEY_ID=$5
AWS_SECRET_ACCESS_KEY=$6

echo environment = \"${TF_ENVIRONMENT}\"

echo "terraform init:"
echo ""
terraform init -backend-config="bucket=${TF_BACKEND_BUCKET}" -backend-config="key=${TF_PROJECT}/infrastructure" -backend-config="region=${TF_AWS_REGION}" -backend=true -force-copy -get=true -input=false

#echo "terraform apply:"
#echo ""
#terraform plan
