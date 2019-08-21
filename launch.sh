set -x

USAGE=$(cat <<-END
    The following environment variables are required
    - AWS_ACCESS_KEY_ID
    - AWS_SECRET_ACCESS_KEY

END
)
echo $USAGE


if [ -z "$AWS_ACCESS_KEY_ID" ]; then
  echo "AWS_ACCESS_KEY_ID MISSING"
  exit 1
fi

if [ -z "$AWS_SECRET_ACCESS_KEY" ]; then
  echo "AWS_SECRET_ACCESS_KEY MISSING"
  exit 1
fi

INFRA_REGION=us-west-2
TF_BUCKETS_REGION=us-east-1
PIPELINE=ugroup-rdso

AWS_ACCOUNT_ID=$(aws sts get-caller-identity --output text --query Account)
echo "aws account id [$AWS_ACCOUNT_ID] aws access key id [$AWS_ACCESS_KEY_ID]"

PROJECT_NAME=ugroup$AWS_ACCOUNT_ID

FLY_TARGET=z${AWS_ACCOUNT_ID}z


echo "concourse url [$CONCOURSE_URL]"
if [ -z "$CONCOURSE_URL" ]; then
  echo "concourse is deploying to account: [$AWS_ACCOUNT_ID] region: [$INFRA_REGION] project: [$PROJECT_NAME] [$CONCOURSE_URL] (~20min depending on infra) [`date`]"
  control-tower -n deploy --iaas AWS --region $INFRA_REGION --worker-size xlarge --worker-type m4 $PROJECT_NAME
fi

while [ -z "$CONCOURSE_URL" ]
do
  sleep 10
  CONCOURSE_URL=$(AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY AWS_DEFAULT_REGION=$INFRA_REGION aws ec2 describe-addresses --filters "Name=tag:name,Values=control-tower-$PROJECT_NAME-atc" --query Addresses[0].PublicIp --output text)
  echo "waiting for concourse, sleeping...."
done


echo "concourse is up [$CONCOURSE_URL] [`date`]"
PASSWORD=$(control-tower info --iaas aws --json --region $INFRA_REGION $PROJECT_NAME | jq -r '.config.concourse_password')
echo "password [$PASSWORD]"

echo "Creating Repository Persistence"
aws s3api create-bucket --bucket infra-$AWS_ACCOUNT_ID  --region $TF_BUCKETS_REGION
aws s3api create-bucket --bucket appdata-$AWS_ACCOUNT_ID  --region $TF_BUCKETS_REGION
aws s3api create-bucket --bucket appdata-$AWS_ACCOUNT_ID-latest  --region $TF_BUCKETS_REGION


# fly login
echo "fly target [$FLY_TARGET]"
fly --target "${FLY_TARGET}" login --insecure --concourse-url https://"$CONCOURSE_URL" --username admin --password "${PASSWORD}"
fly --target "${FLY_TARGET}" sync
fly targets

# infra pipeline
fly set-pipeline -n -p "infra-$PIPELINE" --target $FLY_TARGET -c pipeline-infra.yml -l credentials.template.yml \
  --var=git_username=$GIT_USERNAME \
  --var=git_password=$GIT_PASSWORD \
  --var=aws_account_id=$AWS_ACCOUNT_ID \
  --var=aws_access_key_id=$AWS_ACCESS_KEY_ID \
  --var=aws_secret_access_key=$AWS_SECRET_ACCESS_KEY \
  --var=tf_backend_bucket=infra-$AWS_ACCOUNT_ID \
  --var=sonarqube_rds_password=$PASSWORD \
  --var=bucket_aws_region=$TF_BUCKETS_REGION 

if [ -z "$SONARQUBE_URL" ]; then
  fly up --target "$FLY_TARGET" -p "infra-$PIPELINE"
  fly tj -j "infra-$PIPELINE/Create-Infra" --target $FLY_TARGET
  echo "sonarqube is deploying to account: [$AWS_ACCOUNT_ID] region: [$INFRA_REGION] [$SONARQUBE_URL] (~15min depending on infra) [`date`]"
fi 

while [ -z "$SONARQUBE_URL" ]
do
  sleep 60
  SONARQUBE_URL=http://$(AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY AWS_DEFAULT_REGION=$INFRA_REGION aws elbv2 describe-load-balancers --names "sonarqube-infra-lb"  --query 'LoadBalancers[].DNSName' --output text)
done

echo "wait a little longer for sonarqube to really be up"
sleep 10

echo "sonarqube is up [`date`]"

echo "setting application pipeline"
fly set-pipeline -n -p $PIPELINE -t "$FLY_TARGET" -c pipeline.yml -l credentials.template.yml \
  --var=git_username=$GIT_USERNAME \
  --var=git_password=$GIT_PASSWORD \
  --var=aws_account_id=$AWS_ACCOUNT_ID \
  --var=jwt_secret=$RANDOM$RANDOM$RANDOM$RANDOM$RANDOM$RANDOM$RANDOM$RANDOM$RANDOM$RANDOM$RANDOM$RANDOM$RANDOM$RANDOM$RANDOM$RANDOM \
  --var=business_user_password=$PASSWORD \
  --var=business_supervisor_password=$PASSWORD \
  --var=system_user_password=$PASSWORD \
  --var=aws_access_key_id=$AWS_ACCESS_KEY_ID\
  --var=aws_secret_access_key=$AWS_SECRET_ACCESS_KEY \
  --var=sonarqube_url=$SONARQUBE_URL \
  --var=sonarqube_rds_password=$PASSWORD \
  --var=app_data_bucket=appdata-$AWS_ACCOUNT_ID \
  --var=tf_backend_bucket=infra-$AWS_ACCOUNT_ID 

#kick off hsm deployment
# wait 10 minutes
# we can start hsm while we wait for sonrqube to be ready

fly up --target $FLY_TARGET -p $PIPELINE

fly targets | grep $FLY_TARGET
echo "$SONARQUBE_URL"
echo "pw[$PASSWORD]"

echo "BOOM!"
echo "AKA we are done, be proud of yourself, YOU DID THIS"

