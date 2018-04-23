#!/bin/bash
INIT_MEM="1024m"
MAX_MEM="1024m"
JAR="compiled.jar"

# Set the directory to this script's current directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Clean up before launch
rm -rf $DIR/*.log >/dev/null 2>&1
rm -rf $DIR/*.lck >/dev/null 2>&1
rm -rf $DIR/*.tmp >/dev/null 2>&1

# Execute
java -server -XX:+UseFastAccessorMethods -XX:ParallelGCThreads=8 -XX:+UseAdaptiveGCBoundary -XX:MaxGCPauseMillis=50 -Xms${INIT_MEM} -Xmx${MAX_MEM} -jar ${DIR}/${JAR}
