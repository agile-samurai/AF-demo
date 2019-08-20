#!/usr/bin/env bash
printf "\n*** Signing Cluster CSR ***\n"
CLUSTER_ID=$1
PASS=$2

password=$(echo "${PASS}" | cut -c10-20)
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

printf "\n--- Initializing HSM Cluster\n"
if ! aws cloudhsmv2 initialize-cluster --region $AWS_DEFAULT_REGION --cluster-id ${CLUSTER_ID} --signed-cert file://${CLUSTER_ID}_CustomerHsmCertificate.crt --trust-anchor file://customerCA.crt; then
  exit 1
else
  printf "\n--- HSM Cluster Initialized\n"
fi
