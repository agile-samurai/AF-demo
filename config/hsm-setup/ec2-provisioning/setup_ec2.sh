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
  printf "\n**** Setting up CloudHSM ****\n"

  printf "\n-- Fetching HSM Client\n"
  sudo bash -c "wget https://s3.amazonaws.com/cloudhsmv2-software/CloudHsmClient/EL6/cloudhsm-client-latest.el6.x86_64.rpm"

  printf "\n-- Installing Client\n"
  sudo bash -c "yum install -y ./cloudhsm-client-latest.el6.x86_64.rpm"
  sudo bash -c "yes | yum install expect"
  sudo bash -c "cp /tmp/customerCA.crt /opt/cloudhsm/etc/customerCA.crt"
  sleep 1s
  printf "\n-- Updating configuration files CloudHSM client and command line tools\n"
  sudo bash -c "/opt/cloudhsm/bin/configure -a '$(< ${HSM_IP})'"
  /tmp/expect_script.sh $1 $2
}

setup_microservice() {
  printf "\n**** Setting up hsmgateway microservice ****\n"
  if sudo bash -c "service --status-all | grep -Fq 'hsmgateway'"; then
    printf "\n**** hsmgateway microservice already installed ****\n"
  else
    if [[ ! -d "/var/app" ]]; then
      sudo bash -c "mkdir /var/app"
    fi
    printf "\n-- Moving jar\n"
    sudo bash -c "mv /tmp/hsmgateway.jar /var/app/"
    printf "\n-- Setting permissions"
    chmod 500 /var/app/hsmgateway.jar

    printf "\n-- Setting immutability\n"
    sudo bash -c "chattr +i /var/app/hsmgateway.jar"

    printf "\n-- Linking\n"
    sudo bash -c "ln -s /var/app/hsmgateway.jar /etc/init.d/hsmgateway"

    printf "\n-- Starting hsmgateway microservice\n"
    java -version
    sudo service hsmgateway start
    printf "\n**** Done setting up hsm microservice ****\n"
  fi
}

install_java
setup_cloudhsm_client
setup_microservice