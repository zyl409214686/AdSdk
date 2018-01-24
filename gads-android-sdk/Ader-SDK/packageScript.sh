#!/bin/sh

#  BuildScript.sh
#  GAds-SDK
#
#  Created by guowei huang on 12-2-27.
#  Copyright (c) 2012年 renren. All rights reserved.   

version=$1
# 检验参数
if [ -z $version ];then
echo "版本号没有填写"
exit 1
fi

echo "******* 开始生成发布包 ******"
ant -buildfile obscurejar.xml -Dversion=${version}
echo "*******Ader广告平台CP发布包（ader-android-sdk-${version}.zip）打包完成，已存放在桌面******"




