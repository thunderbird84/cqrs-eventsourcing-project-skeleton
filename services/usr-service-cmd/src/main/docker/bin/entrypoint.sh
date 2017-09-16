#!/bin/sh
exec bin/usr-service-cmd-java \
    -cp $(cat bin/classpath) \
    -XX:+UseG1GC \
    -Xms32m \
    -Xmx128m \
    -Duser.timezone="Asia/Ho_Chi_Minh" \
    -Dfile.encoding="UTF-8" \
    -Duser.language=en \
    -Duser.country=US \
    -Dlogback.configurationFile=$ENV-logback.xml \
    -Djava.io.tmpdir=/tmp \
    -Denv=$ENV \
    -XX:OnOutOfMemoryError=/opt/app/bin/shutdown.sh \
    -XX:+PrintCommandLineFlags \
    -XX:+PrintGC \
    -XX:+PrintGCTimeStamps \
    -Dcom.sun.management.jmxremote.ssl=false \
    -Dcom.sun.management.jmxremote.rmi.port=8889 \
    -Dcom.sun.management.jmxremote.port=8889 \
    -Dcom.sun.management.jmxremote.authenticate=false \
    -Djava.rmi.server.hostname=$(/opt/app/bin/get-ip.sh) \
  robin.dev.examples.UserCmdMain
