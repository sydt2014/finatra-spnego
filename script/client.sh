#!/usr/bin/env bash
#
# You can append your /etc/hosts file with:
#
# <ip> <TAB> spnego-server.example.com spnego-server

ip=${1:-127.0.0.1:8000}
upn=${2:-bob@EXAMPLE.COM}

echo "=== Credentials ${upn} before HTTP connection ==="
kinit -V "${upn}"
klist -fe
echo

echo "=== HTTP connection ${ip} ==="
curl --negotiate -u : -b ~/cookie.txt -c ~/cookie.txt http://${ip}/
echo

echo "=== Credentials ${upn} after HTTP connection ==="
klist -fe
echo
