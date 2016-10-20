#!/usr/bin/env bash
# hadoop fs -rm -r -skipTrash std-dist
rm -rf /tmp/tmp
spark-submit target/spark-std-dist-0.2.jar
cat /tmp/tmp/part-* > ~/Share/ellipse.txt
