TARGET=../../../../../target
java -cp $TARGET/classes:$TARGET/test-classes -agentlib:jdwp=transport=dt_socket,address=localhost:9011,server=y,suspend=y org.drivebydebug.debugtarget.DebugTargetDaemon