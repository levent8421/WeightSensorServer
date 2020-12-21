#!/bin/bash

log_path='/mnt/hd0/scada_wsa/log'

app_path='/sdcard/scada_wsa/app'

jar_path="${app_path}/digital_weight_sensor.jar"

SHOW_LOG=0
SHOW_LOG_SCRIPT='tail-log.sh'
SHUTDOWN=0
SHUTDOWN_SCRIPT='shutdown.sh'

while [[ $# -ge 1 ]];do
    case $1 in
    --show-log|-l )
        SHOW_LOG=1
        shift 1
    ;;
    --shutdown|-s )
        SHUTDOWN=1
        shift 1
    ;;
    *)
        echo "unknown option:$1"
        shift 1
    ;;
    esac
done


if [[ ! -d ${log_path} ]]; then
	mkdir -p ${log_path}
	echo "Create log path success; ${log_path}"
fi

if [[ ${SHUTDOWN} -ge 1 ]]; then
    echo "Auto shutdown with script: ${SHUTDOWN_SCRIPT}"
    ${app_path}/${SHUTDOWN_SCRIPT}
fi

nohup java -jar ${jar_path} 1>${log_path}/out.log 2>${log_path}/err.log &

if [[ $? -ne 0 ]]; then
	echo "[${jar_path}] Start Fail!"
else
	echo "[${jar_path}] Service Start Success!"
fi

if [[ ${SHOW_LOG} -ge 1 ]]; then
    echo 'Tail log:'
    ${app_path}/${SHOW_LOG_SCRIPT}
fi