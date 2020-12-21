#!/bin/bash

java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=10000 -jar /sdcard/scada_wsa/app/digital_weight_sensor.jar

