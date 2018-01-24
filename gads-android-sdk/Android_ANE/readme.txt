
==========================================================================================
该目录下的工程及文件说明：
AderANE_IOS目录：iOS平台ANE静态库工程，用于生成iOS平台ANE静态库文件。使用Xcode打开。

AderANE_Android目录：Android平台ANE jar包工程，用于生成Android平台的ANE jar包。使用Eclipse打开。

AderAS目录：ANE的AS库文件工程，iOS和Android平台共用这个AS工程，使用Flash Builder打开。里面包含AS文 件，及各平台的extension.xml文件。

Sample目录：iOS和Android平台共用的ANE例子工程，用于测试ANE。

AndroidPlatform/iOSPlatform/MultiPlatform:各平台打包目录，包含各自平台的打包脚本build.xml及说明文档。

build_ipa.sh文件：ipa打包脚本，iOS平台及双平台测试时使用。当ane生成成功后，运行该命令，会在Sample/bin-debug目录下生成Sample的ipa文件。

build_apk.xml文件：apk打包脚本，Android平台及双平台测试时使用。当ane生成成功后，运行该命令，会在Sample/bin-debug目录下生成Sample的apk文件。

Flex_Cert.p12文件：生成ANE的签名文件，该文件是各平台共用。


ane打包步骤：
==========================================================================================
iOS平台ANE打包：
1.将最新的iOS广告库文件(包括静态库文件和头文件)拷贝到AderANE_IOS/AderANE目录下
2.如果最新的广告接口有修改，则相应修改AderANE_IOS工程中的相关代码
3.运行AderANE_IOS目录下build_ioslib.sh脚本文件，生成新的ANE静态库文件libRRGAderANE.a于AderANE_IOS/lib目录下
4.使用Flash Builder打开AderAS和Sample工程，修改AderAS/src/extension_ios.xml文件中的版本号
5.如果接口有变化，相应修改AderAS工程中的AS文件和Sample工程中的示例代码
6.运行iOSPlatform目录下的iOS平台ANE打包脚本build.xml,生成ANE并创建发布文件夹，运行脚本时，后面跟着当前SDK的版本号，如sh build.xml 1.1.2
7.更新iOSPlatform目录下的说明文档，并转为pdf格式，放于发布文件夹中，压缩发布文件夹，并发布

==========================================================================================
Android平台ANE打包：
1.将最新的Android广告库文件(jar包)拷贝到AderANE_Android/libs目录下
2.如果最新的广告接口有修改，则相应修改AderANE_Android工程中的相关代码
3.运行AderANE_Android目录下packageScript.sh脚本文件，生成新的ANE库文件RRGAderANE.jar于AderANE_Android目录下
4.使用Flash Builder打开AderAS和Sample工程，修改AderAS/src/extension_android.xml文件中的版本号
5.如果接口有变化，相应修改AderAS工程中的AS文件和Sample工程中的示例代码
6.运行AndroidPlatform目录下的Android平台ANE打包脚本build.xml,生成ANE并创建发布文件夹，运行脚本时，后面跟着当前SDK的版本号，如sh build.xml 1.0.3
7.更新AndroidPlatform目录下的说明文档，并转为pdf格式，放于发布文件夹中，压缩发布文件夹，并发布

==========================================================================================
双平台ANE打包：
1.将最新的Android广告库文件(jar包)拷贝到AderANE_Android/libs目录下；最新的iOS广告库文件(包括静态库文件和头文件)拷贝到AderANE_IOS/AderANE目录下
2.如果最新的广告接口有修改，则相应修改AderANE_Android及AderANE_IOS工程中的相关代码
3.运行AderANE_Android目录下packageScript.sh脚本文件，生成新的ANE库文件RRGAderANE.jar于AderANE_Android目录下；运行AderANE_IOS目录下build_ioslib.sh脚本文件，生成新的ANE静态库文件libRRGAderANE.a于AderANE_IOS/lib目录下
4.使用Flash Builder打开AderAS和Sample工程，修改AderAS/src/extension_multi.xml文件中的版本号
5.如果接口有变化，相应修改AderAS工程中的AS文件和Sample工程中的示例代码
6.运行MultiPlatform目录下的双平台ANE打包脚本build.xml,生成ANE并创建发布文件夹，运行脚本时，后面跟着当前SDK的版本号，如sh build.xml 1.0.3
7.更新MultiPlatform目录下的说明文档，并转为pdf格式，放于发布文件夹中，压缩发布文件夹，并发布

==========================================================================================

其他注意事项：
1.运行build_ipa.xml脚本生成ipa文件时，需要ios的两个证书。现在大家的证书都相同。请将证书放置在/Developer/Certificates/目录下
