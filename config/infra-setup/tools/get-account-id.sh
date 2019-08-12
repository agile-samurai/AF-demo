#!/usr/bin/env bash

python3.6 -c "import boto3,os;print(boto3.client('sts', aws_access_key_id=os.environ['AWS_ACCESS_KEY_ID'],aws_secret_access_key=os.environ['AWS_SECRET_ACCESS_KEY']).get_caller_identity().get('Account'))"
