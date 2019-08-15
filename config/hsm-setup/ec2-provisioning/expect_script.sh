#!/usr/bin/expect

set password [lindex $argv 0]

spawn /opt/cloudhsm/bin/cloudhsm_mgmt_util /opt/cloudhsm/etc/cloudhsm_mgmt_util.cfg

expect "aws-cloudhsm>"
send enable_e2e\n;

expect "aws-cloudhsm>"
send listUsers\n;

expect "aws-cloudhsm>"
send "loginHSM PRECO admin password\n";

expect "aws-cloudhsm>"
send "changePswd PRECO admin $password\n";

expect "Do you want to continue(y/n)?"
send y\n;

expect "aws-cloudhsm>"
send listUsers\n;

expect "aws-cloudhsm>"
send quit\n;

interact