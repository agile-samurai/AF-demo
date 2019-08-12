#!/bin/bash -x
# Set any ECS agent configuration options
echo "ECS_CLUSTER=${cluster_name}" >> /etc/ecs/ecs.config
# path to aws cli
export PATH=~/bin:$PATH

echo "yum update..."
yum update --nogpgcheck -y -q -e 1
echo "yum update...done"
yum install -y aws-cli jq
mkdir /tmp/install
which aws
aws s3 sync s3://sbir-one-nessus/installation /tmp/install

echo "Import tenable..."
rpm --import /tmp/install/tenable-2048.gpg
echo "Install Nessus..."
rpm -ivh /tmp/install/NessusAgent-7.3.2-amzn.x86_64.rpm
/opt/nessus_agent/sbin/nessuscli agent link --host=cloud.tenable.com --groups=sbir_one_${environment} --name=surs-${environment}-ecs --port=443 --key=fce390cb1e3fbf60df0c068c57f1bd8d94800a1f383e7653abd6b1d7308090e1
service nessusagent start
echo "Install Nessus...done"
