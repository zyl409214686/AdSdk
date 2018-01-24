
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

baseDir=$(cd "$(dirname "$0")"; pwd)

#替换文件AndroidManifest.xml和obscurejar.xml中的Ader为新的关键字
for name in *.xml
 do
  sed -i "" s/AderSDKView/"$filename"SDKView/g "$name"
  sed -i "" s/AderDevMode/"$filename"DevMode/g "$name"
  sed -i "" s/AderListener/"$filename"Listener/g "$name"
  sed -i "" s/AderDownloadService/"$filename"DownloadService/g "$name"
  sed -i "" s/AderDownloadItem/"$filename"DownloadItem/g "$name"
 done

cd "$baseDir"
cd ./src/com/rrgame/sdk
#替换sdk目录下文件中的Ader为新的关键字
for name in *.java
 do
  sed -i "" s/AderSDKView/"$filename"SDKView/g "$name"
  sed -i "" s/AderDevMode/"$filename"DevMode/g "$name"
  sed -i "" s/AderListener/"$filename"Listener/g "$name"
  sed -i "" s/AderInterstitialAd/"$filename"InterstitialAd/g "$name"
  sed -i "" s/AderInterstitialAdListener/"$filename"InterstitialAdListener/g "$name"
 done

#修改sdk目录下文件名
mv AderSDKView.java "$filename"SDKView.java
mv AderDevMode.java "$filename"DevMode.java
mv AderListener.java "$filename"Listener.java
mv AderInterstitialAd.java "$filename"InterstitialAd.java
mv AderInterstitialAdListener.java "$filename"InterstitialAdListener.java

#遍历替换sdk子目录文件中的Ader为新的关键字
for name in $(ls)
 do
   if [ -d "$name" ]
   then
    cd "$name"
    for file in $(ls)
     do
      sed -i "" s/AderSDKView/"$filename"SDKView/g "$file"
      sed -i "" s/AderDevMode/"$filename"DevMode/g "$file"
      sed -i "" s/AderListener/"$filename"Listener/g "$file"
      sed -i "" s/AderInterstitialAd/"$filename"InterstitialAd/g "$file"
      sed -i "" s/AderInterstitialAdListener/"$filename"InterstitialAdListener/g "$file"
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

cd ..

#修改service文件名
cd ./webView
mv AderInterstitialAdView.java "$filename"InterstitialAdView.java

cd "$baseDir"
cd ./src/com
#替换AderSDKActivity.java中的Ader为新的关键字
for name in *.java
do
sed -i "" s/AderListener/"$filename"Listener/g "$name"
sed -i "" s/AderInterstitialAd/"$filename"InterstitialAd/g "$name"
sed -i "" s/AderInterstitialAdListener/"$filename"InterstitialAdListener/g "$name"
sed -i "" s/AderSDKView/"$filename"SDKView/g "$name"
done

cd ..
cd ..
cd ./res/layout
for name in *.xml
do
sed -i "" s/AderSDKView/"$filename"SDKView/g "$name"
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
sed -i "" s/AderListener/"$filename"Listener/g "$name"
sed -i "" s/AderInterstitialAd/"$filename"InterstitialAd/g "$name"
sed -i "" s/AderInterstitialAdListener/"$filename"InterstitialAdListener/g "$name"
sed -i "" s/AderSDKView/"$filename"SDKView/g "$name"
done

cd ..
cd ..
cd ..
cd ./res/layout
for name in *.xml
do
sed -i "" s/AderSDKView/"$filename"SDKView/g "$name"
done

#切回到主目录进行打包和混淆
cd "$baseDir"

echo "******* 开始生成发布包 ******"
ant -buildfile obscurejar.xml -Dversion=${version}
echo "*******Ader广告平台CP发布包（ader-android-sdk-${version}.zip）打包完成，已存放在桌面******"


