#!/bin/bash
gcc -I ${JAVA_HOME}/include -I ${JAVA_HOME}/include/linux -shared SerialPort.c -o libserial_port.so
