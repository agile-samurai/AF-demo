from centos
run yum install wget java maven -y
run yum install bind-utils -y
run wget https://s3.amazonaws.com/cloudhsmv2-software/CloudHsmClient/EL7/cloudhsm-client-latest.el7.x86_64.rpm && yum install -y ./cloudhsm-client-latest.el7.x86_64.rpm

run wget https://s3.amazonaws.com/cloudhsmv2-software/CloudHsmClient/EL7/cloudhsm-client-jce-latest.el7.x86_64.rpm && yum install -y ./cloudhsm-client-jce-latest.el7.x86_64.rpm

run mvn install:install-file -Dfile=/opt/cloudhsm/java/cloudhsm-2.0.3.jar -DgroupId=com.cavium -DartifactId=cloudhsm -Dversion=2.0.3 -Dpackaging=jar
