#!/usr/bin/env node

var path = require('path'),
    fs = require('fs');

/**
 * Sets an override value to force the cordova build process to use the actual,
 * specified-in-the-config.xml versionCode.
 */
module.exports = function forceAndroidVersionCode(context) {
  var et = context.requireCordovaModule('elementtree'),
      manifestPath = path.join(context.opts.projectRoot, 'platforms', 'android', 'AndroidManifest.xml'),
      manifestContents = fs.readFileSync(manifestPath, { encoding: 'utf8' }),
      manifest = et.parse(manifestContents),
      versionCode = manifest.getroot().get('android:versionCode'),
      buildExtrasPath = path.join(context.opts.projectRoot, 'platforms', 'android', 'build-extras.gradle');

  fs.writeFileSync(buildExtrasPath, "ext.cdvVersionCode=" + versionCode, { encoding: 'utf8' });
};
