#!/bin/bash

URL_TO_TEST=$1
echo "URL_TO_TEST"
HTTP_STATUS_CODE_TO_WAIT_FOR=$2
TOTAL_TIME_ELAPSED_WHILE_WAITING=0
MAX_WAIT_TIMEOUT=120

while [[ "$(curl -o /dev/null -s -w "%{http_code}" $URL_TO_TEST | tr -d '\n')" != $HTTP_STATUS_CODE_TO_WAIT_FOR ]]
do
	echo Waiting for call to $URL_TO_TEST have a response status code of $HTTP_STATUS_CODE_TO_WAIT_FOR;
	echo Total time elapsed while waiting: $TOTAL_TIME_ELAPSED_WHILE_WAITING seconds

	sleep 4;
	TOTAL_TIME_ELAPSED_WHILE_WAITING=$(($TOTAL_TIME_ELAPSED_WHILE_WAITING + 4))

    if [ "$TOTAL_TIME_ELAPSED_WHILE_WAITING" -gt "$MAX_WAIT_TIMEOUT" ]; then
        echo Timed out waiting for the service within a container to be ready. Exiting now, because this run would fail anyway.
        pwd
        ls -la source
        cat source/dockerlogs.txt
        echo "**********"
        curl http://localhost:8083/api/v1/health/check -v || error=true
        exit 1;
    fi
done
