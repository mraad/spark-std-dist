#! /usr/bin/env bash

DAEMONS="\
cloudera-quickstart-init \
zookeeper-server \
hadoop-hdfs-datanode \
hadoop-hdfs-namenode \
hadoop-hdfs-secondarynamenode \
hadoop-httpfs"

for daemon in ${DAEMONS}; do
    sudo service ${daemon} start
done
