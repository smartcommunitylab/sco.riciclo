smartcampus.vas.rifiuti.mobile
==============================

Cross-platform version of "100% Rifiuti"

## Building apps

- check zipalign and jarsigner are in the system PATH
- check build directory with the credentials file (build.json) and certificates exists

run ``node build_apps_android.js`` to generate signed versions of the android app. The script generates the corresponding apk (profile.apk)