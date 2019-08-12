#!/usr/bin/env bash

yes y | ssh-keygen -t rsa  -b 4096 -f /root/.ssh/id_rsa -N "" > /dev/null

curl -u "$GITHUB_USERNAME:$GITHUB_PASSWORD" \
    --data "{\"title\":\"test\",\"key\":\"`cat /root/.ssh/id_rsa.pub`\"}" \
    https://api.github.com/user/keys
