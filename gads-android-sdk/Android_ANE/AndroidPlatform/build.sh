#!/bin/bash
#编译生成android平台ANE，并生成发布包

version=$1
# 检验参数
if [ -z $version ];then
echo "版本号没有填写"
exit 1
fi

CUR_DIR="`pwd`"
BUILD="build"
RELEASE_PACKAGE_NAME="ader-SDK-ANE-android-${version}"
if [ ! -d ${BUILD} ]
then
mkdir ${BUILD}
fi

if [ ! -d ${BUILD}/iPhone ]
then
mkdir ${BUILD}/iPhone
fi
#copy library to build folder --begin
ANDROID_ANE_JAR_PATH="${CUR_DIR}/../AderANE_Android/RRGAderANE.jar"

AS_NAME="RRGAderAS"
AS_PROJ="${CUR_DIR}/../AderAS"
#unzip swc to build folder --begin
echo unzip -d ${BUILD}/iPhone -o ${AS_PROJ}/bin/${AS_NAME}.swc
unzip -d ${BUILD}/iPhone -o ${AS_PROJ}/bin/${AS_NAME}.swc
rm -rdf ${BUILD}/iPhone/catalog.xml
#unzip swc to build folder --end

if [ ! -d ${BUILD}/android ]
then
mkdir ${BUILD}/android
fi

echo cp ${ANDROID_ANE_JAR_PATH} ${BUILD}/android/
cp ${ANDROID_ANE_JAR_PATH} ${BUILD}/android/

#unzip swc to build folder --begin
echo unzip -d ${BUILD}/android -o ${AS_PROJ}/bin/${AS_NAME}.swc
unzip -d ${BUILD}/android -o ${AS_PROJ}/bin/${AS_NAME}.swc
rm -rdf ${BUILD}/android/catalog.xml
#unzip swc to build folder --end

if [ ! -d ${BUILD}/default ]
then
mkdir ${BUILD}/default
fi

#unzip swc to build folder --begin
echo unzip -d ${BUILD}/default -o ${AS_PROJ}/bin/${AS_NAME}.swc
unzip -d ${BUILD}/default -o ${AS_PROJ}/bin/${AS_NAME}.swc
rm -rdf ${BUILD}/default/catalog.xml
#unzip swc to build folder --end

DEMO_PROJ=${CUR_DIR}/../Sample

ANE_OUT="${DEMO_PROJ}/libs"
EXTENSION_PATH="${AS_PROJ}/src/extension_android.xml"

#copy swc to demo project libs folder --begin
if [ ! -d ${ANE_OUT} ]
then
mkdir ${ANE_OUT}
fi
echo cp ${AS_PROJ}/bin/${AS_NAME}.swc ${ANE_OUT}
cp ${AS_PROJ}/bin/${AS_NAME}.swc ${ANE_OUT}
#copy swc to demo project libs folder --end


echo "Package ane"
/Applications/Adobe\ Flash\ Builder\ 4.6/sdks/4.6.0/bin/adt -package -storetype pkcs12 -keystore ${CUR_DIR}/../Flex_Cert.p12 -storepass jane -target ane "${ANE_OUT}/${AS_NAME}.ane" "${EXTENSION_PATH}" -swc "${AS_PROJ}/bin/${AS_NAME}.swc" -platform iPhone-ARM -C ${BUILD}/iPhone . -platform Android-ARM -C ${BUILD}/android . -platform default -C ${BUILD}/default .
echo "Package ane finish"

#remove build folder --begin
if [ -d ${BUILD} ]
then
rm -rdf ${BUILD}
fi
#打包
#remove build folder --end
RELEASE_FOLDER=${CUR_DIR}/${RELEASE_PACKAGE_NAME}
if [ -d ${RELEASE_FOLDER} ]
then
rm -rdf ${RELEASE_FOLDER}
fi
mkdir ${RELEASE_FOLDER}

#copy swc to Release folder --begin
echo cp ${AS_PROJ}/bin/${AS_NAME}.swc ${RELEASE_FOLDER}
cp ${AS_PROJ}/bin/${AS_NAME}.swc ${RELEASE_FOLDER}
#copy swc to Release folder --end

#copy ane to Release folder --begin
echo cp "${ANE_OUT}/${AS_NAME}.ane" ${RELEASE_FOLDER}
cp "${ANE_OUT}/${AS_NAME}.ane" ${RELEASE_FOLDER}
#copy ane to Release folder --end

#拷贝文档
echo cp "${CUR_DIR}/Ader移动广告ANE接口说明文档.docx" ${RELEASE_FOLDER}
cp "${CUR_DIR}/Ader移动广告ANE接口说明文档.docx" ${RELEASE_FOLDER}
#拷贝Sample工程
echo cp -R "${DEMO_PROJ}" ${RELEASE_FOLDER}
cp -R "${DEMO_PROJ}" ${RELEASE_FOLDER}

#拷贝生成ane的材料文件，方便需要自己生成ane的用户
USER_BUILD_PATH=${RELEASE_FOLDER}/build
if [ ! -d ${USER_BUILD_PATH} ]
then
mkdir ${USER_BUILD_PATH}
fi
#copy library and extension to Release folder --begin
echo cp ${ANDROID_ANE_JAR_PATH} ${USER_BUILD_PATH}
cp ${ANDROID_ANE_JAR_PATH} ${USER_BUILD_PATH}

echo cp ${EXTENSION_PATH} ${USER_BUILD_PATH}
cp ${EXTENSION_PATH} ${USER_BUILD_PATH}
#copy library and extension to Release folder --end

#压缩发布包
#zip -r ${RELEASE_PACKAGE_NAME}.zip ${RELEASE_FOLDER}
