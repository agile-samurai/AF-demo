#!/usr/bin/env bash


for ARGUMENT in "$@"
do

    KEY=$(echo $ARGUMENT | cut -f1 -d=)
    VALUE=$(echo $ARGUMENT | cut -f2 -d=)

    case "$KEY" in
            AWS_ACCESS_KEY_ID)              AWS_ACCESS_KEY_ID=${VALUE} ;;
            AWS_SECRET_ACCESS_KEY)    AWS_SECRET_ACCESS_KEY=${VALUE} ;;
            GITHUB_USERNAME)    GITHUB_USERNAME=${VALUE} ;;
            GITHUB_PASSWORD)    GITHUB_PASSWORD=${VALUE} ;;
            *)
    esac
done


yes | cp pipeline.yml config/infra-setup/scripts/
cd config/infra-setup


docker build . -t mdas/runner && docker run -e AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}  \
                                            -e AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY} \
                                            -e GITHUB_USERNAME=${GITHUB_USERNAME} \
                                            -e GITHUB_PASSWORD=${GITHUB_PASSWORD} \
                                            -it mdas/runner /bin/bash
