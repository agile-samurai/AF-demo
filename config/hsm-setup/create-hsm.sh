#!/usr/bin/env bash

initiate_hsm_setup() {
  if terraform plan -out plan; then
    printf "\n\n=: Terraform plan succeeded\n\n=: Applying plan\n"

    if terraform apply plan; then
      printf "\n\n=: Terraform applied successfully\n\n=: Copying HSM Cluster id\n"

      ID_FILE=cluster_id.txt
      STATE_FILE=cluster_state.txt
      if [[ -f "$STATE_FILE" ]]; then
        if ! grep -Fxq "UNINITIALIZED" ${STATE_FILE}; then
          printf "\n\n=: HSM resource already initialized!\n"
          exit 0
        fi
      fi
      if [[ -f "$ID_FILE" ]]; then
        cp ${ID_FILE} ../post-setup
        printf "\n\n=: HSM Cluster ID file copied to post-setup/\n"
        cd -
      else
        printf "\n\n=: HSM Cluster ID file could not be found\n"
        exit 1
      fi
    else
      printf "\n\n=: Terraform apply failed\n"
      exit 1
    fi
  else
    printf "\n\n=: Terraform plan failed\n"
    exit 1
  fi
}

verify_identity() {
  cd post-setup
  CLUSTER_ID="$(< ${ID_FILE})"

  printf "\n\n=:  Starting identity check.\n\n-: Downloading HSM Cluster Certificates and CSR\n"
  terraform init
  terraform apply -auto-approve
  printf "\n\n=: Retrieving AWS Root Certificate\n"
  if curl -o aws_root.zip https://docs.aws.amazon.com/cloudhsm/latest/userguide/samples/AWS_CloudHSM_Root-G1.zip; then
    unzip -o aws_root.zip #AWS_CloudHSM_Root-G1.crt
  else
    printf "\n\n=: Failed retrieving AWS Root Certificate\n"
    exit 1
  fi

  printf "\n\n=: Retrieving HSM Manufacturer Root Certificate\n"
  if curl -o manufacturer_root.zip https://www.marvell.com/security-solutions/assets/liquid_security_certificate.zip; then
    unzip -o manufacturer_root.zip #liquid_security_certificate.crt
  else
    printf "\n\n=: Failed retrieving HSM Manufacturer hardware Root Certificate\n"
    exit 1
  fi

  printf "\n\n:= Is OpenSSL installed?\n"
  if type openssl > /dev/null; then
    printf "=: YUP! :)\n"
    cat ${CLUSTER_ID}_AwsHardwareCertificate.crt AWS_CloudHSM_Root-G1.crt > ${CLUSTER_ID}_AWS_chain.crt
    cat ${CLUSTER_ID}_ManufacturerHardwareCertificate.crt liquid_security_certificate.crt > ${CLUSTER_ID}_manufacturer_chain.crt

    printf "\n\n=: Verifying against AWS certificate chain\n"
    if openssl verify -CAfile ${CLUSTER_ID}_AWS_chain.crt ${CLUSTER_ID}_HsmCertificate.crt; then
      printf "=: Verified!\n"
    else
      printf "=: Verification with AWS certificate chain failed\n"
      exit 1
    fi

    printf "\n\n=: Verifying against the HSM Manufacturer hardware certificate chain\n"
    if openssl verify -CAfile ${CLUSTER_ID}_manufacturer_chain.crt ${CLUSTER_ID}_HsmCertificate.crt; then
      printf "=: Verified!\n"
    else
      printf "=: Verification with HSM Manufacturer hardware certificate chain failed\n"
      exit 1
    fi

    openssl x509 -in ${CLUSTER_ID}_HsmCertificate.crt -pubkey -noout > ${CLUSTER_ID}_HsmCertificate.pub
    openssl req -in ${CLUSTER_ID}_ClusterCSR.csr -pubkey -noout > ${CLUSTER_ID}_ClusterCSR.pub
    if diff ${CLUSTER_ID}_HsmCertificate.pub ${CLUSTER_ID}_ClusterCSR.pub; then
      printf "\n\n=: HSM resource has been verified!\n"
    else
      printf "\n\n=: HSM resource has failed verification!\n"
    fi
  else
    printf "=: Nope :(\n"
    exit 1
  fi
}

sign_csr() {
  password=test-pass
  country=US
  state=Virginia
  locality=Arlington
  organization=U.Group
  organizationalunit=Technology
  email=odwayne.irving@u.group
  printf "\n\n:= Generating private key\n"
  openssl genrsa -aes256 -passout pass:${password} -out customerCA.key 2048

  printf "\n\n:= Creating self-signed certificate\n"
  openssl req -new -x509 -days 3652 -key customerCA.key -out customerCA.crt -passin pass:${password} -subj "/C=$country/ST=$state/L=$locality/O=$organization/OU=$organizationalunit/emailAddress=$email"

  printf "\n\n:= Signing CSR\n"
  openssl x509 -req -days 3652 -in ${CLUSTER_ID}_ClusterCSR.csr -CA customerCA.crt -CAkey customerCA.key -CAcreateserial -out ${CLUSTER_ID}_CustomerHsmCertificate.crt -passin pass:${password}
}

initialize_hsm() {
   aws cloudhsmv2 initialize-cluster --region us-west-1 --cluster-id ${CLUSTER_ID} --signed-cert file://${CLUSTER_ID}_CustomerHsmCertificate.crt --trust-anchor file://customerCA.crt
}
init() {
  cd initial-setup
  terraform init
}

trap "exit" INT
echo "pid is $$"

printf "\n\n*** Initializing Terraform ***\n"
init
printf "\n\n*** Provisioning HSM infrastructure ***\n"
initiate_hsm_setup
sleep 5s
printf "\n\n*** Verifying HSM identity ***\n"
verify_identity
printf "\n\n*** Signing Cluster CSR ***\n"
sign_csr
printf "\n\n*** Initializing HSM module ***\n"
initialize_hsm

printf "\n\n\n********* Done *********\n"
