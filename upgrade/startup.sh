#!/bin/bash

log_path='/mnt/hd0/scada_wsa/log'

app_path='/sdcard/scada_wsa/app'

jar_path="${app_path}/digital_weight_sensor.jar"

if [ ! -d $log_path ]; then
	mkdir -p $log_path
	echo "Create log path success; ${log_path}"
fi


nohup java -jar $jar_path 1>${log_path}/out.log 2>${log_path}/err.log &

if [ $? -ne 0 ]; then
	echo "[${jar_path}] Start Fail!"
else
	echo "[${jar_path}] Service Start Success!"
fi


