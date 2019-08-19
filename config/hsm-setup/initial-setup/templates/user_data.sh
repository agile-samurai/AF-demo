#!/usr/bin/env bash
HSM_ID=$1
PASS=$2

echo "hsm_Id : ${HSM_ID}"
password=$(echo "${PASS}" | cut -c10-20)

echo "yum update..."
sudo bash -c "yum update --nogpgcheck -y -q -e 1"
echo "yum update...done"
printf "\n**** Installing Java ****\n"
curl -O https://repo.huaweicloud.com/java/jdk/12.0.1+12/jdk-12.0.1_linux-x64_bin.rpm
sudo bash -c "rpm -ivh /home/ec2-user/jdk-12.0.1_linux-x64_bin.rpm"

printf "\n**** Installing CloudHSM Agent and CloudHSM JavaCE Client ****\n"
curl -O https://s3.amazonaws.com/cloudhsmv2-software/CloudHsmClient/EL6/cloudhsm-client-latest.el6.x86_64.rpm
sudo bash -c "yum install -y ./cloudhsm-client-latest.el6.x86_64.rpm"
curl -O https://s3.amazonaws.com/cloudhsmv2-software/CloudHsmClient/EL6/cloudhsm-client-jce-latest.el6.x86_64.rpm
sudo bash -c "yum install -y ./cloudhsm-client-jce-latest.el6.x86_64.rpm"
printf "\n**** Installing expect ****\n"
sudo bash -c "yes | yum install expect"

export HSM_USER=gatewayuser
export HSM_PASSWORD=${password}
export HSM_PARTITION=${HSM_ID}