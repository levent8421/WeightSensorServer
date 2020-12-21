#!/bin/bash

log_dir=/mnt/hd0/scada_wsa/log
current_date=`date +%Y-%m-%d`
log_file="${log_dir}/weight-slot-${current_date}.log"

if [[ ! -f ${log_file} ]]; then
	echo "Could not find today log_file:${log_file}"
	exit 1
fi

tail -f ${log_file}
