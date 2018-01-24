
#  renamePackage.sh
#  AderWall-SDK
#
#  Created by mahua on 13-4-10.
#  Copyright (c) 2013年 renren. All rights reserved.

version=$1
# 检验参数版本号
if [ -z $version ];
then
 echo "版本号没有填写"
 exit 1
fi

filename=$2
# 检验参数
if [ -z $filename ];
then
 echo "替换Ader的新关键字没有填写"
 exit 1
fi

packagename=$3

#修改的包名称

baseDir=$(cd "$(dirname "$0")"; pwd)


#替换文件AndroidManifest.xml和obscurejar.xml中的Ader为新的关键字
for name in *.xml
 do
  sed -i "" s/AderDevMode/"$filename"DevMode/g "$name"
  sed -i "" s/AderDownloadService/"$filename"DownloadService/g "$name"
  sed -i "" s/AderDownloadItem/"$filename"DownloadItem/g "$name"
 done

cd "$baseDir"
cd ./src/com/rrgame/sdk
#替换sdk目录下文件中的Ader为新的关键字
for name in *.java
 do
  sed -i "" s/AderDevMode/"$filename"DevMode/g "$name"
  sed -i "" s/AderScreen/"$filename"Screen/g "$name"
  sed -i "" s/AderScreenListener/"$filename"ScreenListener/g "$name"
 done

#修改sdk目录下文件名
mv AderDevMode.java "$filename"DevMode.java
mv AderScreen.java "$filename"Screen.java
mv AderScreenListener.java "$filename"ScreenListener.java

#遍历替换sdk子目录文件中的Ader为新的关键字
for name in $(ls)
 do
   if [ -d "$name" ]
   then
    cd "$name"
    for file in $(ls)
     do
      sed -i "" s/AderDevMode/"$filename"DevMode/g "$file"
      sed -i "" s/AderScreen/"$filename"Screen/g "$file"
      sed -i "" s/AderScreenListener/"$filename"ScreenListener/g "$file"
      sed -i "" s/AderDownloadItem/"$filename"DownloadItem/g "$file"
      sed -i "" s/AderDownloadService/"$filename"DownloadService/g "$file"
     done
    cd ..
   fi

 done

cd ./download
#修改下载文件的文件名

mv AderDownloadItem.java "$filename"DownloadItem.java
mv AderDownloadService.java "$filename"DownloadService.java

cd "$baseDir"
cd ./src/com
#替换AderSDKActivity.java中的Ader为新的关键字
for name in *.java
do
sed -i "" s/AderScreen/"$filename"Screen/g "$name"
sed -i "" s/AderScreenListener/"$filename"ScreenListener/g "$name"
done

cd "$baseDir"
cd ../AderSDKPackageRelease/Sample
#sample工程中替换文件AndroidManifest.xml中的Ader为新的关键字
for name in *.xml
do
sed -i "" s/AderDownloadService/"$filename"DownloadService/g "$name"
done

cd ./src/com/rrgame

#sample工程中替换MainActivity.java文件中的Ader为新的关键字
for name in *.java
do
sed -i "" s/AderDevMode/"$filename"DevMode/g "$name"
sed -i "" s/AderScreen/"$filename"Screen/g "$name"
sed -i "" s/AderScreenListener/"$filename"ScreenListener/g "$name"
done



#kang
#删除用来加密的java类
cd "$baseDir"
cd ./src/com/dynamic/sdk/readcm
rm LoadClass.java
rm ReadJar.java

#替换版本号
cd "$baseDir"
cd ./src/com/rrgame/sdk/adutils
sed -i "" -e "s/SDKVERSION[^\S\r\n]*=[^\S\r\n]*\".*\";/SDKVERSION\ =\ \"${version}\";/g" AderPublicUtils.java

#替换包名相关代码
if [ -z $packagename ];
then
   packagename="rrgame"
   echo "包名没有填写"
else

   #修改AndroidManifest.xml包名
   cd "$baseDir"
   sed -i "" -e "s/com.rrgame/com."${packagename}"/g" AndroidManifest.xml

   cd "$baseDir"
   echo "******* 替换java文件里的引用包名路径以及包文件夹名 ******"
   ant -buildfile packageRename_ant.xml -Dreplace.pkg.name=${packagename}

   #修改sample相关包名
   cd "$baseDir"
   cd ../AderSDKPackageRelease/Sample
   sed -i "" -e "s/com.rrgame/com."${packagename}"/g" AndroidManifest.xml

   cd "$baseDir"
   echo "******* 重命名sample里的包名 ******"
   cd ../AderSDKPackageRelease/Sample
   ant -buildfile sampleRename_ant.xml -Dreplace.pkg.name=${packagename}

fi
#kang


#切回到主目录进行打包和混淆
cd "$baseDir"
echo "******* 开始生成发布包 ******"
ant -buildfile obscurejar.xml -Dversion=${version} -Dreplace.pkg.name=${packagename}
echo "*******Ader广告平台CP发布包（ader-android-sdk-${version}.zip）打包完成，已存放在桌面******"

