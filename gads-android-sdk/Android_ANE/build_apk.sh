#!/bin/bash
CUR_DIR="`pwd`"
APP_NAME="Sample"
APP_DIR="${CUR_DIR}/${APP_NAME}/bin-debug"

EXTDIR="${APP_DIR}/ext"
if [ ! -d ${EXTDIR} ]
then
mkdir ${EXTDIR}
fi
LIB_PATH=${CUR_DIR}/${APP_NAME}/libs
echo cp ${LIB_PATH}/RRGAderAS.ane ${EXTDIR}/
cp ${LIB_PATH}/RRGAderAS.ane ${EXTDIR}/

KEYPATH=${CUR_DIR}/AndroidPlatform/"Flash_android.p12"
KEYPWD="hill"
cd $APP_DIR

echo "Package apk"
/Applications/Adobe\ Flash\ Builder\ 4.6/sdks/4.6.0/bin/adt -package -target apk -storetype pkcs12 -keystore ${KEYPATH} -storepass ${KEYPWD} ${APP_NAME}.apk ${APP_NAME}-app.xml ${APP_NAME}.swf -extdir ext
echo "Package apk finish"

#remove ext folder --begin
if [ -d ${EXTDIR} ]
then
rm -rdf ${EXTDIR}
fi
#remove ext folder --end

/Developer/Android/android-sdks/platform-tools/adb install -r ${APP_NAME}.apk