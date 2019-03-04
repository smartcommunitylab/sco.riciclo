
set profile=%1
set password=%2
set alias=%3
set sourceDir=%CD%

if exist ".\build\%profile%-release.apk" (
	del ".\build\%profile%-release.apk"
)

cd "..\..\riciclo_running\%profile%"
set rootDir=%CD%
echo "Android build %profile%"
call npm i -D -E @ionic/v1-toolkit
node .\hooks\before_prepare\005_move_proto_config_file.js "%rootDir%"
node .\hooks\before_prepare\010_replace_text.js "%rootDir%"
call ionic cordova platform rm android
call ionic cordova platform add android@7.0.0
call ionic cordova build android --release
copy .\platforms\android\app\build\outputs\apk\release\app-release-unsigned.apk %sourceDir%\build\%profile%-release.apk

cd %sourceDir%
if exist ".\build\%profile%.apk" (
	del ".\build\%profile%.apk"
)

jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore .\build\%profile%.keystore -storepass %password% .\build\%profile%-release.apk %alias%
jarsigner -verify -certs -verbose .\build\%profile%-release.apk
zipalign -v 4 .\build\%profile%-release.apk .\build\%profile%.apk
del .\build\%profile%-release.apk

