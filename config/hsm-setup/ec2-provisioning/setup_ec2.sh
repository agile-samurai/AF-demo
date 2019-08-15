#!/usr/bin/env bash

printf "**** Fetching HSM Client ****"
sudo bash -c "wget https://s3.amazonaws.com/cloudhsmv2-software/CloudHsmClient/EL6/cloudhsm-client-latest.el6.x86_64.rpm"
HSM_IP=/tmp/hsmip.txt

printf '**** Installing Client ****'
sudo bash -c "yum install -y ./cloudhsm-client-latest.el6.x86_64.rpm"
sudo bash -c "yes | yum install expect"
sudo bash -c "cp /tmp/customerCA.crt /opt/cloudhsm/etc/customerCA.crt"
sleep 1s
printf '**** Updating configuration files CloudHSM client and command line tools ****'
sudo bash -c "/opt/cloudhsm/bin/configure -a '$(< ${HSM_IP})'"

/tmp/expect_script.sh $1