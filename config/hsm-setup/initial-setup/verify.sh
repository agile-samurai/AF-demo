#!/usr/bin/env bash

printf "\n*** Verifying HSM identity ***\n"
CLUSTER_ID=$1

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
  if ! cat ${CLUSTER_ID}_AwsHardwareCertificate.crt AWS_CloudHSM_Root-G1.crt > ${CLUSTER_ID}_AWS_chain.crt; then
    exit 1
  fi
  if ! cat ${CLUSTER_ID}_ManufacturerHardwareCertificate.crt liquid_security_certificate.crt > ${CLUSTER_ID}_manufacturer_chain.crt; then
    exit 1
  fi

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
    exit 1
  fi
else
  printf "\n--- Nope :(\n"
  exit 1
fi