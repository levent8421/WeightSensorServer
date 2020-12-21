#!/bin/bash

if [[ ! $# -eq 1 ]]; then
	echo 'Require 1 param [version]'
	exit 1
fi

version=$1
sync_server='10.233.89.82'
sync_server_user='root'
sync_server_path='/sdcard/scada_wsa/upgrade'

sync_server_ssh_path="${sync_server_user}@${sync_server}:${sync_server_path}/upgrade-${version}.zip"

scp ${sync_server_ssh_path} ./
