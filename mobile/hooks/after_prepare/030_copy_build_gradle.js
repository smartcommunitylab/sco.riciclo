#!/usr/bin/env node

var fs = require('fs');
var path = require('path');

var rootdir = process.argv[2];

//PROCEDURE TO COPY THE PROTO CONFIG FILE TO THE WORKING CONFING FILE
var srcfile = path.join(rootdir, "config", "build.gradle");
var destfile = path.join(rootdir, "platforms/android", "build.gradle");
//var destfile = path.join(rootdir, "test.xml");
var destdir = path.dirname(destfile);
if (fs.existsSync(srcfile) && fs.existsSync(destdir)) {
    fs.createReadStream(srcfile).pipe(fs.createWriteStream(destfile));
}
