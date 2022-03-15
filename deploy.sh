#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/Mople
cd $REPOSITORY

APP_NAME=boilerplate-0.0.1-SNAPSHOT.jar


CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 종료할것 없음."
else
  echo "> kill -9 $CURRENT_PID"
  sudo  kill -9 $CURRENT_PID
  sleep 5
fi

sudo nohup java -jar  $APP_NAME > ./nohup.out 2>&1 &

