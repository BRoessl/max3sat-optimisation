@ECHO OFF
ECHO Compiling...
call mvn clean install -DskipTests
PAUSE
