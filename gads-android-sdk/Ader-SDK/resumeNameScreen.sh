#改名恢复脚本
#用法：sh resumeNameScript.sh 新关键字 原关键字
#如：sh resumeNameScript.sh AderWall hello

filename=$1
# 检验参数
if [ -z $filename ];
then
 echo "替换Ader的新关键字没有填写"
 exit 1
fi

originname=$2
# 检验参数
if [ -z $originname ];
then
echo "替换Ader的原关键字没有填写"
exit 1
fi

baseDir=$(cd "$(dirname "$0")"; pwd)

#替换文件AndroidManifest.xml和obscurejar.xml中的AderWall为新的关键字
for name in *.xml
 do
  sed -i "" s/${originname}DevMode/"$filename"DevMode/g "$name"
  sed -i "" s/${originname}DownloadItem/"$filename"DownloadItem/g "$name"
  sed -i "" s/${originname}DownloadService/"$filename"DownloadService/g "$name"
 done

cd "$baseDir"
cd ./src/com/rrgame/sdk

#替换sdk目录下文件中的Ader为新的关键字
for name in *.java
 do
  sed -i "" s/${originname}DevMode/"$filename"DevMode/g "$name"
  sed -i "" s/${originname}Screen/"$filename"Screen/g "$name"
  sed -i "" s/${originname}ScreenListener/"$filename"ScreenListener/g "$name"
  done

#修改rrgame目录下文件名
mv ${originname}DevMode.java "$filename"DevMode.java
mv ${originname}Screen.java "$filename"Screen.java
mv ${originname}ScreenListener.java "$filename"ScreenListener.java

#遍历替换rrgame子目录文件中的AderWall为新的关键字
for name in $(ls)
 do
   if [ -d "$name" ]
   then
    cd "$name"
    for file in $(ls)
     do
      sed -i "" s/${originname}DevMode/"$filename"DevMode/g "$file"
      sed -i "" s/${originname}Screen/"$filename"Screen/g "$file"
      sed -i "" s/${originname}ScreenListener/"$filename"ScreenListener/g "$file"
      sed -i "" s/${originname}DownloadService/"$filename"DownloadService/g "$file"
      sed -i "" s/${originname}DownloadItem/"$filename"DownloadItem/g "$file"
     done
    cd ..
   fi

 done

#修改下载文件的文件名
cd ./download

mv ${originname}DownloadItem.java "$filename"DownloadItem.java
mv ${originname}DownloadService.java "$filename"DownloadService.java


cd "$baseDir"
cd ./src/com

#替换AderWallSDKActivity.java文件中的AderWall为新的关键字
for name in *.java
 do
  sed -i "" s/${originname}Screen/"$filename"Screen/g "$name"
  sed -i "" s/${originname}ScreenListener/"$filename"ScreenListener/g "$name"
done

cd "$baseDir"
cd ../AderSDKPackageRelease/Sample
#sample工程中替换文件AndroidManifest.xml中的AderWall为新的关键字
for name in *.xml
 do
  sed -i "" s/${originname}DownloadService/"$filename"DownloadService/g "$name"
 done

cd ./src/com/rrgame

#sample工程中替换MainActivity.java文件中的AderWall为新的关键字
for name in *.java
 do
  sed -i "" s/${originname}DevMode/"$filename"DevMode/g "$name"
  sed -i "" s/${originname}Screen/"$filename"Screen/g "$name"
  sed -i "" s/${originname}ScreenListener/"$filename"ScreenListener/g "$name"
 done

cd "$baseDir"
