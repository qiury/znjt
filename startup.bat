@echo off
echo '开始启动数据同步系统'
java -jar -Xms1024m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m target/deliverc-1.0.jar
