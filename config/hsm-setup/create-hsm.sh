#!/usr/bin/env bash

#HSM_USER_PASS=$(dd if=/dev/urandom bs=1 count=32 2>/dev/null | base64 -b 0 | rev | cut -b 2- | rev)

PASS_FILE=pass.txt
STATE_FILE=cluster_state.txt

init() {
  printf "\n*** Initializing Terraform ***\n"
  cd initial-setup
  terraform init
}

initiate_hsm_setup() {
  printf "\n*** Provisioning HSM infrastructure ***\n"
  if terraform plan -out plan -var="hsm_controller=${HOST_IP}" -var="region=${HSM_REGION}"; then
    printf "\n--- Terraform plan succeeded\n--- Applying plan\n"

    if terraform apply plan; then
      printf "\n--- Terraform applied successfully\n\n--- Copying files\n"

      ID_FILE=cluster_id.txt
      HSM_ID_FILE=hsm_id.txt
      KEY_FILE=key.pem
      IP_FILE=ec2ip.txt
      HSM_IP_FILE=hsmip.txt

      if [[ -f "$STATE_FILE" ]]; then
        if grep -Fxq "UNINITIALIZED" ${STATE_FILE}; then
           if [[ -f "$ID_FILE" ]]; then
            mv -f ${ID_FILE} ../post-setup
          else
            printf "\n--- HSM Cluster ID file could not be found\n"
            exit 1
          fi

          if [[ -f "$PASS_FILE" ]]; then
            cp -f ${PASS_FILE} ../post-setup
            mv -f ${PASS_FILE} ../ec2-provisioning
          else
            printf "\n--- Admin pass file could not be found\n"
            exit 1
          fi

          if [[ -f "$KEY_FILE" ]]; then
            mv -f ${KEY_FILE} ../ec2-provisioning
          else
            printf "\n--- Key file could not be found\n"
            exit 1
          fi

          if [[ -f "$IP_FILE" ]]; then
            mv -f ${IP_FILE} ../ec2-provisioning
          else
            printf "\n--- IP file could not be found\n"
            exit 1
          fi

          if [[ -f "$HSM_IP_FILE" ]]; then
            mv -f ${HSM_IP_FILE} ../ec2-provisioning
          else
            printf "\n--- HSM IP file could not be found\n"
            exit 1
          fi

          if [[ -f "$HSM_ID_FILE" ]]; then
            mv -f ${HSM_ID_FILE} ../ec2-provisioning
          else
            printf "\n--- HSM ID file could not be found\n"
            exit 1
          fi

          printf "\n--- Files copied successful"
          cd -
          verify_identity
        elif grep -Fxq "INITIALIZED" ${STATE_FILE}; then
          printf "\n--- HSM resource already initialized!\n"
          cd -
        else
          printf "\n--- HSM resource already active!\n"
          exit 0
        fi
      fi
    else
      printf "\n--- Terraform apply failed\n"
      exit 1
    fi
  else
    printf "\n--- Terraform plan failed\n"
    exit 1
  fi
}

verify_identity() {
  printf "\n*** Verifying HSM identity ***\n"
  cd post-setup
  CLUSTER_ID="$(< ${ID_FILE})"

  printf "\n---  Starting identity check.\n\n--- Downloading HSM Cluster Certificates and CSR\n"
  terraform init
  terraform apply -auto-approve
  printf "\n--- Retrieving AWS Root Certificate\n"
  if curl -o aws_root.zip https://docs.aws.amazon.com/cloudhsm/latest/userguide/samples/AWS_CloudHSM_Root-G1.zip; then
    unzip -o aws_root.zip #AWS_CloudHSM_Root-G1.crt
  else
    printf "\n--- Failed retrieving AWS Root Certificate\n"
    exit 1
  fi

  printf "\n--- Retrieving HSM Manufacturer Root Certificate\n"
  if curl -o manufacturer_root.zip https://www.marvell.com/security-solutions/assets/liquid_security_certificate.zip; then
    unzip -o manufacturer_root.zip #liquid_security_certificate.crt
  else
    printf "\n--- Failed retrieving HSM Manufacturer hardware Root Certificate\n"
    exit 1
  fi

  printf "\n--- Is OpenSSL installed?\n"
  if type openssl > /dev/null; then
    printf "\n--- YUP! :)\n"
    cat ${CLUSTER_ID}_AwsHardwareCertificate.crt AWS_CloudHSM_Root-G1.crt > ${CLUSTER_ID}_AWS_chain.crt
    cat ${CLUSTER_ID}_ManufacturerHardwareCertificate.crt liquid_security_certificate.crt > ${CLUSTER_ID}_manufacturer_chain.crt

    printf "\n--- Verifying against AWS certificate chain\n"
    if openssl verify -CAfile ${CLUSTER_ID}_AWS_chain.crt ${CLUSTER_ID}_HsmCertificate.crt; then
      printf "\n--- Verified!\n"
    else
      printf "\n--- Verification with AWS certificate chain failed\n"
      exit 1
    fi

    printf "\n--- Verifying against the HSM Manufacturer hardware certificate chain\n"
    if openssl verify -CAfile ${CLUSTER_ID}_manufacturer_chain.crt ${CLUSTER_ID}_HsmCertificate.crt; then
      printf "\n--- Verified!\n"
    else
      printf "\n--- Verification with HSM Manufacturer hardware certificate chain failed\n"
      exit 1
    fi

    openssl x509 -in ${CLUSTER_ID}_HsmCertificate.crt -pubkey -noout > ${CLUSTER_ID}_HsmCertificate.pub
    openssl req -in ${CLUSTER_ID}_ClusterCSR.csr -pubkey -noout > ${CLUSTER_ID}_ClusterCSR.pub
    if diff ${CLUSTER_ID}_HsmCertificate.pub ${CLUSTER_ID}_ClusterCSR.pub; then
      printf "\n--- HSM resource has been verified!\n"
    else
      printf "\n--- HSM resource has failed verification!\n"
    fi
  else
    printf "\n--- Nope :(\n"
    exit 1
  fi
}

sign_csr() {
  printf "\n*** Signing Cluster CSR ***\n"
  echo "$(< ${PASS_FILE})"
  ADMIN_PASS="$(< ${PASS_FILE})"
  password=$(echo "${ADMIN_PASS}" | cut -c10-20)
  country=US
  state=Virginia
  locality=Arlington
  organization=U.Group
  organizationalunit=Technology
  email=odwayne.irving@u.group

  printf "\n--- Generating private key\n"
  if ! openssl genrsa -aes256 -passout pass:${password} -out customerCA.key 2048; then
    exit 1
  fi

  printf "\n--- Creating self-signed certificate\n"
  if ! openssl req -new -x509 -days 3652 -key customerCA.key -out customerCA.crt -passin pass:${password} -subj "/C=$country/ST=$state/L=$locality/O=$organization/OU=$organizationalunit/emailAddress=$email"; then
    exit 1
  fi

  printf "\n--- Signing CSR\n"
  if ! openssl x509 -req -days 3652 -in ${CLUSTER_ID}_ClusterCSR.csr -CA customerCA.crt -CAkey customerCA.key -CAcreateserial -out ${CLUSTER_ID}_CustomerHsmCertificate.crt -passin pass:${password}; then
    exit 1
  fi
}

initialize_hsm() {
  printf "\n*** Initializing HSM module ***\n"

  if aws cloudhsmv2 initialize-cluster --region us-west-1 --cluster-id ${CLUSTER_ID} --signed-cert file://${CLUSTER_ID}_CustomerHsmCertificate.crt --trust-anchor file://customerCA.crt; then
    mv -f customerCA.crt ../ec2-provisioning
    cd -
    return 0
  else
    return 1
  fi
}

provision_client() {
  cd ec2-provisioning
  terraform init
  terraform plan -out plan
  terraform apply plan
}

trap "exit" INT
echo "pid is $$"

init
initiate_hsm_setup
sleep 2s
if grep -Fxq "ACTIVE" ${STATE_FILE}; then
  exit 0
elif grep -Fxq "INITIALIZED" ${STATE_FILE}; then
  provision_client
else
  verify_identity
  sign_csr

  if initialize_hsm; then
    provision_client
  fi
fi

printf "\n\n********* Done *********\n"