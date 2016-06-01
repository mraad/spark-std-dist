#!/usr/bin/env bash
hadoop fs -rm -r -skipTrash std-dist
spark-submit target/spark-std-dist-0.1.jar
