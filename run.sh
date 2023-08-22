#!/bin/sh
LOG4J=apache-log4j-2.18.0-bin

job=$1
ip=$2
port=$3
numtasks=$4

java -cp lib/zookeeper-3.7.1.jar:lib/zookeeper-jute-3.7.1.jar:lib/slf4j-api-1.7.35.jar:lib/slf4j-reload4j-1.7.35.jar:$LOG4J/log4j-slf4-impl-2.18.0.jar:$LOG4J/log4j-core-2.18.0.jar:$LOG4J/log4j-api-2.18.0.jar:$LOG4J/log4j-1.2-api-2.18.0.jar:. $job $ip:$port $numtasks

