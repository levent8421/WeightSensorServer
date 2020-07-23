#!/bin/bash

jar=/sdcard/scada_wsa/app/digital_weight_sensor.jar

echo "Stopping ${jar} ......"

find_pid(){
	ps_line=`jps -ml|grep ${1}`
	pid=${ps_line%${1}}
	echo $pid
}

pid=$(find_pid ${jar})

echo "Java Process PID=[${pid}]"

kill_pid(){
	pid=$1
	if [ -z $pid ]; then
		echo 'Can not found process!'
		exit -1
	fi
	kill -9 ${pid}
	if [ $? -ne 0 ]; then
		echo "Kill Process fail, return=${?}"
		return -1
	else
		echo 'Kill Process Success!'
		return 0
	fi
}

kill_pid $pid


if [ $? -ne 0 ]; then
	echo 'Kill Done!'
	exit $?
else
	exit 0
fi
