#!/usr/bin/env bash
export POINTS=/tmp/points.csv
echo "CASE_ID,LON,LAT" > ${POINTS}
awk -f points.awk >> ${POINTS}
