#!/usr/bin/env bash
HSM_ID=$1
PASS=$2
HSM_IP=$3
CLUSTER_STATE=$4
REGION=$5

printf "\n*** Cluster State: ${CLUSTER_STATE}***\n"

setup_cloudhsm_client() {
  sleep 5s
  printf "\n**** Setting up CloudHSM ****\n"

  printf "\n-- Updating configuration files CloudHSM client and command line tools\n"
  sudo bash -c "cp /tmp/customerCA.crt /opt/cloudhsm/etc/customerCA.crt"
  sleep 1s
  sudo bash -c "/opt/cloudhsm/bin/configure -a ${HSM_IP}"
  /tmp/expect_script.sh "${password}"
}

run_client_and_gateway() {
  printf "\n-- Starting CloudHSM Client\n"
  sudo bash -c "start cloudhsm-client"
  printf "\n**** CloudHSM Ready for work! ****\n"
  printf "\n**** Starting Gateway Service! ****\n"
  export HSM_PASSWORD=$(echo '${aws_iam_user_login_profile.admin.encrypted_password}' | cut -c10-20) && export HSM_PARTITION=${aws_cloudhsm_v2_hsm.cloudhsm_v2_hsm.hsm_id} && export HSM_USER=gatewayuser && nohup java -Djava.library.path=/opt/cloudhsm/lib -jar /tmp/hsmgateway-0.0.1-SNAPSHOT.jar &
  printf "\n**** Gateway Service started ****\n"

}
#setup_env
setup_cloudhsm_client

if ${CLUSTER_STATE} == INITIALIZED; then
  if setup_cloudhsm_client; then
    sleep 5;
      run_client_and_gateway
  fi
elif ${CLUSTER_STATE} == ACTIVE; then
  run_client_and_gateway
fi