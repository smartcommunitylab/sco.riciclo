var ncp = require('ncp').ncp;
var fs = require('fs-extra');

ncp.limit = 16;

var profile = null;
console.log('init copy_running');

if (process.argv.length < 3) {
    console.error("Profile name arg is missing, read curren_profile.txt...");
    profile = fs.readFileSync("config/current_profile.txt");
    console.log("Found "+profile);
} else {
    profile = process.argv[2];
    console.log("Not Found "+profile);
}

if (profile == null) {
    console.error("Profile name is missing!");
} else {

    fs.copy("../mobile", "../../RUNNING/"+profile, function (err) {
        console.log('fs.copy -- from: ../mobile - to: ../../RUNNING/'+profile);

        if (err) {
          console.error("copy app err: "+err);
        } else {
            console.log('copy app done!');

            ncp("config/instances/"+profile+"/www", "../../RUNNING/"+profile+"/www", function (err) {
                 console.log('fs.copy -- from: config/instances/'+profile+'/www - to: ../../RUNNING/'+profile+'/www');

                 if (err) {
                   console.error("copy config err: "+err);
                 }

                console.log('copy config done!');
            });

        }
    });

}

console.log('end copy_running');
