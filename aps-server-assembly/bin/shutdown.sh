#!/bin/sh

cd $(dirname $0)/
cd ..
. bin/commons.sh

echo "Prepare stopping APS server"
if [ ! -z "$APS_INSTANCE_PID" ]; then
    if [ -f "$APS_INSTANCE_PID" ]; then
        if [ -s "$APS_INSTANCE_PID" ]; then
            kill -0 `cat "$APS_INSTANCE_PID"` >/dev/null 2>&1
            if [ $? -gt 0 ]; then
                echo "PID file found but no matching process was found. Stop aborted."
                exit 1
            fi
        else
            echo "PID file is empty and has been ignored."
            exit 1
        fi
    else
        echo "$APS_INSTANCE_PID file does not exist. Is APS Server running? Stop aborted."
        exit 1
    fi
fi
if [ -z "$APS_INSTANCE_PID" ]; then
    echo "Kill failed: \$APS_INSTANCE_PID not set"
else
    if [ -f "$APS_INSTANCE_PID" ]; then
        PID=`cat "$APS_INSTANCE_PID"`
        echo "Killing APS Server with the PID: $PID"
        kill $PID
        echo "Waiting APS Server stopping"
        while kill -0 $PID 2>/dev/null; do sleep 1; done
        echo "APS Server stopped"
        rm -f "$APS_INSTANCE_PID" >/dev/null 2>&1
        if [ $? != 0 ]; then
            echo "APS Server was killed but the PID file could not be removed."
        fi
    fi
fi
