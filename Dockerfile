FROM centos:7
# run with 'docker run -v $(pwd):/tmp -it <image-name>' mounts working directory in container

RUN yum -y update \
  && yum install -y sudo curl unzip wget git-core \
  && yum clean all

RUN wget https://releases.hashicorp.com/terraform/0.12.5/terraform_0.12.5_linux_amd64.zip \
  && unzip terraform_0.12.5_linux_amd64.zip && rm terraform_0.12.5_linux_amd64.zip \
  && wget https://github.com/EngineerBetter/control-tower/releases/download/0.7.3/control-tower-linux-amd64 \ 
  && mv terraform /usr/local/bin/ \
  && mv control-tower-linux-amd64 /usr/local/bin/control-tower \
  && chmod +x /usr/local/bin/control-tower \
  && wget -O fly https://github.com/concourse/fly/releases/download/v4.2.2/fly_linux_amd64 && mv fly /usr/local/bin/ \
  && chmod +x /usr/local/bin/fly

RUN wget https://github.com/duo-labs/cloudmapper/archive/2.6.5.zip \
  && unzip 2.6.5.zip \
  && chmod +x cloudmapper-2.6.5

# control tower dependancies
RUN yum install -y gcc gcc-c++ ruby ruby-devel mysql-devel postgresql-devel postgresql-libs sqlite-devel libxslt-devel libxml2-devel patch openssl epel-release
RUN yum install -y autoconf automake libtool python3-devel.x86_64 python3-tkinter python-pip jq awscli
# (Debian, Ubuntu etc.):
RUN gem install yajl-ruby

RUN wget https://github.com/stedolan/jq/releases/download/jq-1.5/jq-linux64 -O jq
RUN chmod +x jq
RUN mv jq /usr/local/bin

RUN mkdir /app

COPY launch.sh app/.
COPY destroy-launch.sh app/.
COPY credentials.template.yml app/.

WORKDIR /app

RUN terraform version
RUN fly -v
RUN control-tower -v
RUN git --version
RUN ruby -v


CMD ["/bin/bash", "launch.sh"]
