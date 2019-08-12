FROM ubuntu:16.04

RUN apt-get update
RUN apt-get install -y software-properties-common vim
RUN add-apt-repository ppa:jonathonf/python-3.6
RUN apt-get update

RUN apt-get install -y build-essential python3.6 python3.6-dev python3-pip python3.6-venv \
                       curl unzip wget git -y  && wget https://releases.hashicorp.com/terraform/0.11.13/terraform_0.11.13_linux_amd64.zip \
                       && unzip terraform_0.11.13_linux_amd64.zip \
                       && mv terraform /usr/local/bin/

RUN apt-get install -y git

RUN python3.6 -m pip install pip --upgrade
RUN python3.6 -m pip install wheel click awscli pyyaml==3.13 boto3

RUN wget -O fly https://github.com/concourse/concourse/releases/download/v3.14.1/fly_linux_amd64 && mv fly /usr/local/bin/

RUN apt-get update && apt-get install

COPY scripts/ /scripts
COPY tools/ /tools
COPY template.credentials.yaml /template.credentials.yaml

RUN mkdir /mdas-challenge
COPY ./build.sh /build.sh
COPY ./destroy.sh /destroy.sh

WORKDIR /

RUN chmod +x /usr/local/bin/fly

ENTRYPOINT ["./build.sh"]
#ENTRYPOINT ["./destroy.sh"]
