#!/bin/sh

cd $(dirname $0)/
cd ..
. bin/commons.sh

echo "Prepare starting APS server"
if [ ! -z "$APS_INSTANCE_PID" ]; then
    if [ -f "$APS_INSTANCE_PID" ]; then
        if [ -s "$APS_INSTANCE_PID" ]; then
            echo "Existing PID file found during start."
                if [ -r "$APS_INSTANCE_PID" ]; then
                    PID=`cat "$APS_INSTANCE_PID"`
                    ps -p $PID >/dev/null 2>&1
                    if [ $? -eq 0 ] ; then
                        echo "APS Server appears to still be running with PID $PID. Start aborted."
                        exit 1
                    else
                        echo "Removing/clearing stale PID file."
                        rm -f "$APS_INSTANCE_PID" >/dev/null 2>&1
                        if [ $? != 0 ]; then
                            if [ -w "$APS_INSTANCE_PID" ]; then
                                cat /dev/null > "$APS_INSTANCE_PID"
                            else
                                echo "Unable to remove or clear stale PID file. Start aborted."
                                exit 1
                            fi
                        fi
                    fi
                else
                    echo "Unable to read PID file. Start aborted."
                    exit 1
                fi
        else
            rm -f "$APS_INSTANCE_PID" >/dev/null 2>&1
            if [ $? != 0 ]; then
                if [ ! -w "$APS_INSTANCE_PID" ]; then
                    echo "Unable to remove or write to empty PID file. Start aborted."
                    exit 1
                fi
            fi
        fi
    fi
fi
rm -rf $APS_WORKING_DIR/*
if [ -f $APS_APP_DIR ];then
    TAR_FILES=$APS_APP_DIR
else
    TAR_FILES=$APS_APP_DIR/*.tar.gz
fi

for f in $TAR_FILES; do
    if [ -f $f ]; then
        echo "unpacking $f"
        tar xzvf $f -C $APS_WORKING_DIR >/dev/null 2>/dev/null
    fi
done


for f in  $APS_HOME/lib/*.jar; do
    if [ ! -z $CLASSPATH ]; then
        CLASSPATH=${CLASSPATH}:
    fi
    CLASSPATH=${CLASSPATH}${f};
done

nohup java -server -Xmn512M -Xmx1G -Xms1G -XX:PermSize=64M -XX:MaxPermSize=64m \
    -Daps.home=${APS_HOME} -Dlog4j.configuration=file://${APS_HOME}/conf/log4j.xml \
    -Daps.config.file=file://${APS_HOME}/conf/aps.yaml \
    -cp $CLASSPATH com.anjuke.aps.server.zmq.ApsZMQServerMain >>$APS_HOME/logs/aps.out 2>&1 &

PID=$!
echo $PID > $APS_INSTANCE_PID
echo "start APS server with pid $PID finish"
