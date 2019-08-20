#!/usr/bin/env bash
HSM_ID=$1
PASS=$2
HSM_IP=$3

password=$(echo "${PASS}" | cut -c10-20)

setup_cloudhsm_client() {
  sleep 5s
  printf "\n**** Setting up CloudHSM ****\n"
  printf "\n**** password: ****: ${password}\n"
  printf "\n**** ID: ****: ${HSM_ID}\n"

  printf "\n-- Updating configuration files CloudHSM client and command line tools\n"
  sudo bash -c "cp /tmp/customerCA.crt /opt/cloudhsm/etc/customerCA.crt"
  sleep 1s
  sudo bash -c "/opt/cloudhsm/bin/configure -a ${HSM_IP}"
  /tmp/expect_script.sh "${password}"

  printf "\n-- Starting CloudHSM Client\n"
  sudo bash -c "start cloudhsm-client"

  printf "**** CloudHSM Ready for work! ****"
}

#setup_env
setup_cloudhsm_client