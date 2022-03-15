#!/usr/bin/env bash
REPOSITORY=/home/ubuntu/Mople


APP_NAME=boilerplate-0.0.1-SNAPSHOT.jar


CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 종료할것 없음."
else
  echo "> kill -15 $CURRENT_PID"
  sudo  kill -15 $CURRENT_PID
  sleep 5
fi

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)


chmod +x $JAR_NAME

nohup java -jar  $JAR_NAME >  $REPOSITORY/nohup.out 2>&1 &
