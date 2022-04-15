#!/bin/bash
# chmod u+x wsl2.sh

set_proxy() {
  echo export https_proxy="http://win.sbx0.cn:11112"
  echo export http_proxy="http://win.sbx0.cn:11112"
  echo "vim /root/.bashrc"
  echo "source /root/.bashrc"
}

set_wsl2_ip_to_windows_host() {
  win_hosts_path="/mnt/c/Windows/System32/drivers/etc/hosts"
  wsl_domain="wsl2.sbx0.cn"
  wsl_ip=$(ifconfig eth0 | grep -w inet | awk '{print $2}')
  if grep -wq "$wsl_domain" $win_hosts_path; then
    win_hosts=$(sed -s "s/.* $wsl_domain/$wsl_ip $wsl_domain/g" $win_hosts_path)
    echo "$win_hosts" >$win_hosts_path
  else
    echo "$wsl_ip $wsl_domain" >>$win_hosts_path
  fi

  win_domain="win.sbx0.cn"
  win_ip=$(cat /etc/resolv.conf | grep "nameserver" | awk '{print $2}')
  if grep "$win_domain" $win_hosts_path; then
    echo "win_ip ${win_ip}"
    wsl_hosts=$(sed -s "s/.* $win_domain/$win_ip $win_domain/g" $win_hosts_path)
    echo "$wsl_hosts" >$win_hosts_path
  else
    echo "$win_ip $win_domain" >>$win_hosts_path
  fi
}

set_windows_ip_to_wsl2_host() {
  wsl_hosts_path="/etc/hosts"
  win_domain="win.sbx0.cn"
  win_ip=$(cat /etc/resolv.conf | grep "nameserver" | awk '{print $2}')
  if grep "$win_domain" $wsl_hosts_path; then
    echo "win_ip ${win_ip}"
    wsl_hosts=$(sed -s "s/.* $win_domain/$win_ip $win_domain/g" $wsl_hosts_path)
    echo "$wsl_hosts" >$wsl_hosts_path
  else
    echo "$win_ip $win_domain" >>$wsl_hosts_path
  fi

  wsl_domain="wsl2.sbx0.cn"
  wsl_ip=$(ifconfig eth0 | grep -w inet | awk '{print $2}')
  if grep "$wsl_domain" $wsl_hosts_path; then
    echo "wsl_ip ${wsl_ip}"
    win_hosts=$(sed -s "s/.* $wsl_domain/$wsl_ip $wsl_domain/g" $wsl_hosts_path)
    echo "$win_hosts" >$wsl_hosts_path
  else
    echo "$wsl_ip $wsl_domain" >>$wsl_hosts_path
  fi
}

case "$1" in
"set_proxy" | "1")
  set_proxy
  ;;
"set_wsl2_ip_to_windows_host" | "2")
  set_wsl2_ip_to_windows_host
  ;;
"set_windows_ip_to_wsl2_host" | "3")
  set_windows_ip_to_wsl2_host
  ;;
*)
  echo '1.set_proxy | 2.set_wsl2_ip_to_windows_host | 3.set_windows_ip_to_wsl2_host'
  ;;
esac
