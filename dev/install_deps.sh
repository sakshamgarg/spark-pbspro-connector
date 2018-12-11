#!/bin/sh

git clone https://github.com/pbspro/pbspro /tmp/pbspro
cd /tmp/pbspro
./autogen.sh
./configure  --prefix /opt/pbs
make
sudo make install
sudo /opt/pbs/libexec/pbs_postinstall
sudo vi /etc/pbs.conf
sudo chmod 4755 /opt/pbs/sbin/pbs_iff /opt/pbs/sbin/pbs_rcp
sudo /etc/init.d/pbs start
#source /etc/profile.d/pbs.sh

qstat
