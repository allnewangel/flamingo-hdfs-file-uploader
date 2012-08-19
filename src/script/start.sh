#!/bin/sh
### ====================================================================== ###
##                                                                          ##
##  Flamingo HDFS File Uploader Startup Script                              ##
##                                                                          ##
### ====================================================================== ###

FLAMINGO_OPTS="-Duploader.job.xml=conf/example.xml"
JAVA_OPTS="-DUPLOADER -server -Xms256m -Xmx256m -XX:MaxPermSize=128m ${FLAMINGO_OPTS}"

### $Id: start.sh $ ###

DIRNAME=`dirname $0`
PROGNAME=`basename $0`
GREP="grep"

# Setup the Listener HOME
if [ "x$UPLOADER_HOME" = "x" ]; then
    UPLOADER_HOME=`cd $DIRNAME/..; pwd`
fi
export UPLOADER_HOME

# Setup the JVM
if [ "x$JAVA" = "x" ]; then
    if [ "x$JAVA_HOME" != "x" ]; then
    JAVA="$JAVA_HOME/bin/java"
    else
    JAVA="java"
    fi
fi

# Setup the classpath
UPLOADER_CLASSPATH=conf:
for FILENAME in lib/*.jar
do
UPLOADER_CLASSPATH=${UPLOADER_CLASSPATH}:${FILENAME}
done
UPLOADER_CLASSPATH=${UPLOADER_CLASSPATH}:${CLASSPATH}

# Setup the startup class
STARTUP_CLASS=org.openflamingo.uploader.Starter

# Display our environment
echo "========================================================================="
echo ""
echo "  Flamingo HDFS File Uploader Bootstrap Environment"
echo ""
echo "  UPLOADER_HOME: $UPLOADER_HOME"
echo ""
echo "  JAVA: $JAVA"
echo ""
echo "  JAVA_OPTS: $JAVA_OPTS"
echo ""
echo "  CLASSPATH: $UPLOADER_CLASSPATH"
echo ""
echo "========================================================================="
echo ""

${JAVA} ${JAVA_OPTS} -classpath ${UPLOADER_CLASSPATH} ${STARTUP_CLASS}