#!/usr/bin/expect

set admin_password [lindex $argv 0]
set hsm_user_password [lindex $argv 1]

spawn /opt/cloudhsm/bin/cloudhsm_mgmt_util /opt/cloudhsm/etc/cloudhsm_mgmt_util.cfg

expect "aws-cloudhsm>"
send enable_e2e\n;

expect "aws-cloudhsm>"
send listUsers\n;

expect "aws-cloudhsm>"
send "loginHSM PRECO admin password\n";

expect "aws-cloudhsm>"
send changePswd PRECO admin $admin_password\n;

expect "Do you want to continue(y/n)?"
send y\n;

expect "aws-cloudhsm>"
send listUsers\n;

expect "aws-cloudhsm>"
send createUser CU gateway-user $hsm_user_password\n;

expect "Do you want to continue(y/n)?"
send y\n;

expect "aws-cloudhsm>"
send quit\n;

interact