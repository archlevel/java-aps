
if [ -f "bin/env.sh" ];then
    . bin/env.sh
fi

if [ -z $APS_HOME ]; then
    export APS_HOME=`pwd`
fi

if [ ! -d $APS_HOME ]; then
    echo "APS_HOME ${APS_HOME} not exists"
    exit -1
fi

if [ -z $APS_INSTANCE_PID ]; then
    export APS_INSTANCE_PID="$APS_HOME/conf/aps.pid"
fi

touch $APS_INSTANCE_PID>/dev/null 2>&1
if [ ! -f $APS_INSTANCE_PID ];then
    echo "Cannot create APS_INSTANCE_PID file on ${APS_INSTANCE_PID}"
    exit -1
fi

if [ -z $APS_APP_DIR ]; then
    export APS_APP_DIR="$APS_HOME/aps_apps"
fi

if [ ! -d $APS_APP_DIR ] && [ ! -f $APS_APP_DIR ]; then
    echo "APS_APP_DIR $APS_APP_DIR not exists"
    exit -1
fi

if [ -z $APS_WORKING_DIR ]; then
    export APS_WORKING_DIR="$APS_HOME/temp"
fi

if [ ! -d $APS_WORKING_DIR ]; then
    echo "APS_WORKING_DIR $APS_WORKING_DIR not exists"
    exit -1
fi

echo "APS_HOME           = $APS_HOME"
echo "APS_INSTANCE_PID   = $APS_INSTANCE_PID"
echo "APS_APP_DIR        = $APS_APP_DIR"
echo "APS_WORKING_DIR    = $APS_WORKING_DIR"
