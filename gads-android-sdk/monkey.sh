#!/bin/sh
#
#  Created by jingle li on 13-3-18.
#  Copyright (c) 2012年 renren. All rights reserved.   

pacakage=$1

if [ -z $pacakage ]
then
echo "*************包名没有传入*************" 
exit 1
fi

throttle=300 #间隔时间 毫秒
pcttouch=35
pctmotion=25
pcttrackball=5
pctnav=15
pctmajornav=15
pctsyskeys=5
pctappswitch=0
pctanyevent=0
eventnum=3000  #测试次数

# set runtime=%date:~0,4%_%date:~5,2%_%date:~8,2%_%time:~0,2%_%time:~3,2%

adb -d shell monkey -s 5 -p ${pacakage} --throttle $throttle --pct-touch $pcttouch --pct-motion $pctmotion --pct-trackball $pcttrackball --pct-nav $pctnav --pct-majornav $pctmajornav --pct-syskeys $pctsyskeys --pct-appswitch $pctappswitch --pct-anyevent $pctanyevent --monitor-native-crashes -v -v ${eventnum} >monkey_log.txt







