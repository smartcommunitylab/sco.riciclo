var ncp = require('ncp').ncp;
var fs = require('fs-extra');

ncp.limit = 16;

var profile = null;
console.log('check 1');

if (process.argv.length < 3) {
    console.error("Profile name arg is missing, read curren_profile.txt...");
    profile = fs.readFileSync("config/current_profile.txt");
    console.log("Found "+profile);
} else {
    profile = process.argv[2];
}

if (profile == null) {
    console.error("Profile name is missing!");
} else {
    console.log('check 2');

    fs.copy("../mobile", "../../riciclo_running/"+profile, function (err) {
        console.log('check 3');

        if (err) {
          console.error("copy app err: "+err);
        } else {
            console.log('copy app done!');

            ncp("config/instances/"+profile+"/www", "../../riciclo_running/"+profile+"/www", function (err) {
                 console.log('check 3.1');

                 if (err) {
                   console.error("copy config err: "+err);
                 }

                console.log('copy config www done!');
            });
						
            ncp("config/instances/"+profile+"/resources", "../../riciclo_running/"+profile+"/resources", function (err) {
                 console.log('check 3.2');

                 if (err) {
                   console.error("copy config err: "+err);
                 }

                console.log('copy config resources done!');
            });

        }
    });

   //ncp("../mobile", "../../RUNNING/", function (err) {
   //    console.log('check 3');

   //    if (err) {
   //      console.error("copy app err: "+err);
   //    }
   //
   //    console.log('copy app done!');

   //    ncp("config/instances/"+profile+"/www", "../../RUNNING/mobile/www", function (err) {
   //         console.log('check 3.1');

   //         if (err) {
   //           return console.error("copy config err: "+err);
   //         }

   //         console.log('copy config done!');
   //    });
   //});

    console.log('check 4');

}

console.log('check 5');
