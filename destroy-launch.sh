echo "begin deleting all environments from aws acccount"

if [ -z "$AWS_SECRET_ACCESS_KEY" ]; then
  echo "please set AWS_SECRET_ACCESS_KEY"
  exit 0
fi

if [ -z "$AWS_ACCESS_KEY_ID" ]; then
   echo "please set AWS_ACCESS_KEY_ID"
  exit 0
fi


PROJECT_NAME=ugroup-rdso

AWS_ACCOUNT_ID=$(aws sts get-caller-identity --output text --query Account)
echo "deleting [$AWS_ACCOUNT_ID]"
echo "deleting [$AWS_ACCESS_KEY_ID]"

PIPELINE=ugroup-rdso

# fly --target $PIPELINE login --insecure --concourse-url $CONCOURSE_URL --username admin --password $PASSWORD

# fly tj destroy-all --target $PIPELINE -p $PIPELINE

# control-tower destroy --iaas AWS $PROJECT_NAME

# aws s3api delete-bucket --bucket infra-$AWS_ACCOUNT_ID  --region $TF_BUCKETS_REGION
# aws s3api delete-bucket --bucket appdata-$AWS_ACCOUNT_ID  --region $TF_BUCKETS_REGION
# aws s3api list-buckets --region $TF_BUCKETS_REGION

# aws iam create-account-alias --account-alias  "alias$AWS_ACCOUNT_ID"

export $AWS_ACCOUNT_ID; ./aws-nuke --no-dry-run --access-key-id $AWS_ACCOUNT_ID --secret-access-key $AWS_SECRET_ACCESS_KEY  -c config.txt

echo "end deleting all environments from aws acccount"

