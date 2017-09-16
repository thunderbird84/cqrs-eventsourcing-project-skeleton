#!/bin/sh

if type ip > /dev/null 2>&1; then
  IP_ADDR=$(ip -o a|grep -F 'inet 10.'|cut -d' ' -f7|cut -d'/' -f1|head -n1|xargs)
  if [ -z "$IP_ADDR" ]; then
    # fallback to  172.xx ip
    IP_ADDR=$(ip -o a|grep -F 'inet 172.'|cut -d' ' -f7|cut -d'/' -f1|head -n1|xargs)
  fi
fi

if [ -z "$IP_ADDR" ]; then
  # fallback to loopback
  IP_ADDR="127.0.0.1"
fi

echo $IP_ADDR
