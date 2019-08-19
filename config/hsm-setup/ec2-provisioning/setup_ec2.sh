#!/usr/bin/env bash

install_java() {
  printf "\n**** Installing Java ****\n"
  curl -O https://repo.huaweicloud.com/java/jdk/12.0.1+12/jdk-12.0.1_linux-x64_bin.rpm
  sudo bash -c "rpm -ivh /home/ec2-user/jdk-12.0.1_linux-x64_bin.rpm"
  echo $JAVA_HOME
  java -version
  which java
}

setup_cloudhsm_client() {
  HSM_IP=/tmp/hsmip.txt
  HSM_ID=/tmp/hsm_id.txt
  PASS=/tmp/pass.txt
  GATEWAY_PASS="$(echo "$(< ${PASS})" |  cut -c1-30)"
  printf "****************** ${GATEWAY_PASS}"
  printf "\n**** Setting up CloudHSM ****\n"

  sudo bash -c "echo 'HSM_USER=gatewayuser' > ~/.bashrc"
  sudo bash -c "echo 'HSM_PASSWORD=${GATEWAY_PASS}' > ~/.bashrc"
  sudo bash -c "echo 'HSM_PARTITION=$(< ${HSM_ID})' > ~/.bashrc"

  printf "\n-- Fetching HSM Client\n"
  sudo bash -c "wget https://s3.amazonaws.com/cloudhsmv2-software/CloudHsmClient/EL6/cloudhsm-client-latest.el6.x86_64.rpm"
  printf "\n-- Installing Client\n"
  sudo bash -c "yum install -y ./cloudhsm-client-latest.el6.x86_64.rpm"

  printf "\n-- Fetching HSM Java Client\n"
  sudo bash -c "wget https://s3.amazonaws.com/cloudhsmv2-software/CloudHsmClient/EL6/cloudhsm-client-jce-latest.el6.x86_64.rpm"
  printf "\n-- Installing Java Client\n"
  sudo bash -c "yum install -y ./cloudhsm-client-jce-latest.el6.x86_64.rpm"

  printf "\n-- Installing expect\n"
  sudo bash -c "yes | yum install expect"

  printf "\n-- Updating configuration files CloudHSM client and command line tools\n"
  sudo bash -c "cp /tmp/customerCA.crt /opt/cloudhsm/etc/customerCA.crt"
  sleep 1s
  sudo bash -c "/opt/cloudhsm/bin/configure -a '$(< ${HSM_IP})'"
  /tmp/expect_script.sh "${GATEWAY_PASS}"

  printf "\n-- Starting CloudHSM Client\n"
  sudo bash -c "start cloudhsm-client"

  printf "**** CloudHSM Ready for work! ****"
}

install_java
setup_cloudhsm_client
#setup_microservice