#!/bin/bash
# chmod u+x wsl2.sh

host_ip=$(cat /etc/resolv.conf | grep "nameserver" | cut -f 2 -d " ")
echo export https_proxy="http://${host_ip}:11112"
echo export http_proxy="http://${host_ip}:11112"
echo "vim /root/.bashrc"
echo "source /root/.bashrc"
