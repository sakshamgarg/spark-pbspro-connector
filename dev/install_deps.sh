#!/bin/sh

quit() {
  echo "$1"
  cleanup
  exit 1
}

git clone https://github.com/pbspro/pbspro /tmp/pbspro
cd /tmp/pbspro

./autogen.sh
./configure  --prefix /opt/pbs

make
sudo make install

sudo /opt/pbs/libexec/pbs_postinstall
sudo chmod 4755 /opt/pbs/sbin/pbs_iff /opt/pbs/sbin/pbs_rcp

/opt/pbs/sbin/pbs_iff -t $(hostname) 15001 || quit "PBSPro could not connect to the server"

sudo sh -c "echo \"PBS_SERVER=$(hostname)\" >> /etc/pbs.conf"
sudo /etc/init.d/pbs start || quit "Could not start PBS"

. /etc/profile.d/pbs.sh || quit "Could not source profile.d/pbs.sh"
qstat || quit "Could not qstat"

