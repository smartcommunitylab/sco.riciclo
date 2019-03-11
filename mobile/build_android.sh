#!/bin/sh

profile="$1"

if [ -f build/$profile-release.apk ]; then
  rm build/$profile-release.apk
fi

cd "../../riciclo_running/$profile"
echo "Android build $profile"

echo $profile > config/current_profile.txt
node hooks/before_prepare/005_move_proto_config_file.js $(pwd)
node hooks/before_prepare/010_replace_text.js $(pwd)
npm i -D -E @ionic/v1-toolkit
# ionic cordova platform rm android
# ionic cordova platform add android@7.0.0
# ionic cordova build android --release
cd -

mv ../../riciclo_running/$profile/platforms/android/app/build/outputs/apk/release/app-release-unsigned.apk build/$profile-release.apk
if [ -f build/$profile.apk ]; then
  rm build/$profile.apk
fi                       
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore build/$profile.keystore -storepass $2 build/$profile-release.apk $3
jarsigner -verify -certs -verbose build/$profile-release.apk
zipalign -v 4 build/$profile-release.apk build/$profile.apk
rm build/$profile-release.apk

cd -
