##!/usr/bin/env bash
#
#REPOSITORY=/home/ubuntu/Mople
#
#APP_NAME=boilerplate
#
#CURRENT_PID=$(pgrep -fl $APP_NAME | grep java | awk '{print $1}')
#if [ -z $CURRENT_PID ]
#then
#  echo "> 종료할것 없음."
#
#else
#  echo "> kill -15 $CURRENT_PID"
#  sudo  kill -15 $CURRENT_PID
#  sleep 5
#fi
#
#
#rm  -rf $REPOSITORY/boilerplate-0.0.1-SNAPSHOT-plain.jar
#
#
#JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)
#echo "> JAR Name: $JAR_NAME"
#chmod +x $JAR_NAME
#
#echo "> 실행 "
#sudo nohup java -jar  $JAR_NAME >  $REPOSITORY/nohup.out 2>&1 &
#echo "> ========================================================"
