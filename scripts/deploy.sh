#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/Mople

APP_NAME=boilerplate

CURRENT_PID=$(pgrep -fl $APP_NAME | grep java | awk '{print $1}')
if [ -z $CURRENT_PID ]
then
  echo "> 종료할것 없음." 
  
else
  echo "> kill -15 $CURRENT_PID"
  sudo  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 어플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"
echo "> $JAR_NAME에 실행권한 추가"
chmod +x $JAR_NAME

echo "> 실행 "
nohup java -jar  $JAR_NAME >  $REPOSITORY/nohup.out 2>&1 &
echo "> ========================================================"
