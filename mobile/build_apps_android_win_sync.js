var fs = require('fs-extra');
var rimraf = require("rimraf");
var copydir = require('copy-dir');
const { spawn } = require( 'child_process' );

var profiles = JSON.parse(fs.readFileSync('config/profiles.json', 'utf-8'));
var credentials = JSON.parse(fs.readFileSync('build/build.json', 'utf-8'));

for (let j = 2; j < process.argv.length; j++) { 
	var profile = process.argv[j]; 
	console.log('Processing '+ profile);
	console.log('------------------------------------');
	if (!credentials[profile]) {
		console.log('credentials not found');
		continue;
	}
	console.log('remove previous folder');
	rimraf.sync('../../riciclo_running/'+profile);
	console.log('remove previous folder done');
	console.log('copy mobile folder');
	copydir.sync("../mobile", "../../riciclo_running/"+profile+'');
	console.log('copy mobile folder done');
	fs.copy("config/instances/"+profile+"/www", "../../riciclo_running/"+profile+"/www");
	console.log('copy www folder done');
	fs.copy("config/instances/"+profile+"/resources", "../../riciclo_running/"+profile+"/resources");
	console.log('copy config resources done');
	console.log('start build script');
	var ex = spawn('build_android.bat', [profile, credentials[profile].password, credentials[profile].alias]);    
	ex.stdout.on('data', function (data) {
		console.log('stdout: ' + data);
	});
	ex.stderr.on( 'data', data => {
		console.log( `${profile} stderr: ${data}` );
	});
	ex.on( 'close', code => {
		console.log( `${profile} child process exited with code ${code}` );
	});
}

