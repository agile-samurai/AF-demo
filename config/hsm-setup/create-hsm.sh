#!/usr/bin/env bash

###
#
###

STATE_FILE=initial-setup/cluster_state.txt

init() {
  printf "\n*** Building gateway service ***\n"
  cd ../../services/hsmgateway
  docker build -t build-image:latest .
  docker run -v $(pwd):/build build-image bash -c "mvn clean install -f /build/pom.xml -DskipTests"
  cp target/hsmgateway*.jar ../../config/hsm-setup/initial-setup
  printf "\n*** Gateway service built***\n"

  printf "\n*** Initializing Terraform ***\n"
  cd ../../config/hsm-setup/initial-setup
  terraform init
}

initiate_hsm_setup() {
  
  printf "\n*** Provisioning HSM infrastructure ***\n"
  if TF_VAR_hsm_controller=$(dig @resolver1.opendns.com ANY myip.opendns.com +short -4) terraform plan -out plan -target=aws_cloudhsm_v2_hsm.cloudhsm_v2_hsm -target=local_file.ec2_key && terraform apply plan; then

    if TF_VAR_hsm_controller=$(dig @resolver1.opendns.com ANY myip.opendns.com +short -4) terraform plan -out plan; then
      printf "\n--- Terraform second stage plan succeeded\n--- Applying plan\n"

      if terraform apply plan; then
        printf "\n--- Terraform second stage applied successfully\n\n--- Copying files\n"
      else
        printf "\n--- Terraform apply failed\n"
        exit 1
      fi
    else
      printf "\n--- Terraform plan failed\n"
      exit 1
    fi
  fi
}

trap "exit" INT
echo "pid is $$"

SUCCESS_NON_INITIALIZE=5
init

i=1
until [ $i -gt $SUCCESS_NON_INITIALIZE ]
do
    initiate_hsm_setup
    sleep 2s
    if grep -Fxq "ACTIVE" ${STATE_FILE}; then
      printf "\n\n********* Done *********\n"
      exit 0
    fi
    i=$((i+1))
done

terraform destroy -force
printf "\n*** An error occured, please try again ***\n"

