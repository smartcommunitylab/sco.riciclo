var ncp = require('ncp').ncp;
var fs = require('fs-extra');
var copydir = require('copy-dir');
var rimraf = require("rimraf");
const { spawn } = require( 'child_process' );

ncp.limit = 160;

var profiles = JSON.parse(fs.readFileSync('config/profiles.json', 'utf-8'));
var credentials = JSON.parse(fs.readFileSync('build/build.json', 'utf-8'));

Object.keys(profiles).forEach((profile) => {
    if (!credentials[profile]) return;
    console.log('Processing '+ profile);

    rimraf('../../riciclo_running/'+profile, () => {
        copydir.sync("../mobile", "../../riciclo_running/"+profile+'');
        ncp("config/instances/"+profile+"/www", "../../riciclo_running/"+profile+"/www", function (err2) {
            if (err2) {
              console.error("copy config err: "+err2);
            } else {
               ncp("config/instances/"+profile+"/resources", "../../riciclo_running/"+profile+"/resources", function (err) {
                   if (err) {
                     console.error("copy config err: "+err);
                   } else {
                       console.log('copy config resources done!');
                       var ex = spawn('sh', ['build_android.sh', profile, credentials[profile].password, credentials[profile].alias]);    
                       ex.stderr.on( 'data', data => {
                           console.log( `${profile} stderr: ${data}` );
                       } );
                       
                       ex.on( 'close', code => {
                           console.log( `${profile} child process exited with code ${code}` );
                       } );
                   }
              });       
            }

       });    
    });
});
