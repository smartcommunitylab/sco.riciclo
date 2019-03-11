#!/bin/sh

# for profile in "ASIA" "ASIA-ROT" "ASIA-PAGANELLA" "ASIA-ADIGE" "ASIA-VALLELAGHI" "ASIA-CEMBRA" "ESACOM" "ASVO" "ASP" "GIUDICARIE"; do
for profile in  "ESACOM" "ASVO" "ASP" "GIUDICARIE"; do
rm -R ../../riciclo_running/$profile
cp -R ../mobile ../../riciclo_running/$profile
cp -R config/instances/$profile/www/* ../../riciclo_running/$profile/www
cp -R config/instances/$profile/resources/* ../../riciclo_running/$profile/resources

cd ../../riciclo_running/$profile
echo "iOS build $profile"

for filename in config/instances/$profile/www/img/*; do
  rm www/img/$(basename $filename)
done
cp -R config/instances/$profile/www/img/* www/img

sed -i -e 's/%//' config/profiles.json
sed -i -e 's/"display_name": "100/"display_name": "100%/' config/profiles.json

echo $profile > config/current_profile.txt
node hooks/before_prepare/005_move_proto_config_file.js $(pwd)
node hooks/before_prepare/010_replace_text.js $(pwd)
npm i -D -E @ionic/v1-toolkit
ionic cordova platform rm android
ionic cordova platform rm ios
ionic cordova platform add ios
ionic cordova build ios --release

cd -
done