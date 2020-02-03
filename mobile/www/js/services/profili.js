angular.module('rifiuti.services.profili', [])

.factory('Profili', function (DataManager, $rootScope, $filter, Raccolta, Calendar, Utili, $q) {
    var ProfiliFactory = {};

    var toMessage = function (typemap) {
        var lines = [];
        for (var t in typemap) {
            lines.push(t);
        }

        if (lines.length == 1) {
            return lines[0];
        } else if (lines.length > 1) {
            var msg = lines[0];
            for (var i = 1; i < lines.length; i++) {
                msg += ',' + lines[i].replace('Porta a porta', '');
            }
            return msg;
        }

        return null;
    };

    ProfiliFactory.updateNotifications = function () {
        if (cordova && cordova.plugins && cordova.plugins.notification) {
            console.log('initializing notifications...');
            cordova.plugins.notification.local.cancelAll();

            var id = 0;
            var notifArray = [];
            var promises = [];
            $rootScope.profili.forEach(function (p) {
                if (!!p.settings && !p.settings.enableNotifications) return;
                promises.push(Raccolta.notificationCalendar(p));
            });
            $q.all(promises).then(function(datas) {
                datas.forEach(function(data) {
                    if (data) {
                        var daymap = {};
                        // notifications for 12 month range
                        var dFrom = new Date();
                        var dTo = new Date();
                        dTo.setMonth(dTo.getMonth() + 12);
                        if (dTo.getFullYear() > dFrom.getFullYear()) {
                            dTo.setMonth(1);
                            dTo.setDate(1);
                        }

                        data.forEach(function (n) {
                            n.orarioApertura.forEach(function (cal) {
                                for (var i = 0; i < cal.dates.length; i++) {
                                    var dStr = cal.dates[i];
                                    var currDate = new Date(Date.parse(dStr));
                                    if (currDate.getTime() >= dFrom.getTime() && currDate.getTime() < dTo.getTime()) {
                                        var hours = Math.floor(n.ora / 3600);
                                        var mins = (n.ora % 3600) / 60;
                                        var targetDate = new Date(currDate.getFullYear(), currDate.getMonth(), currDate.getDate() - 1, hours, mins, 0, 0);
                                        if (targetDate.getTime() > dFrom.getTime()) {
                                            if (!(dStr in daymap)) {
                                                daymap[dStr] = {
                                                    id: id++,
                                                    title: $filter("translate")("tomorrow_at") + n.comune,
                                                    text: {},
                                                    smallIcon: 'res://drawable-hdpi/notification.png',
                                                    prio: 'high',
                                                    // autoCancel: true,
                                                    firstAt: targetDate
                                                };
                                            }
                                            daymap[dStr].text[n.tipologiaPuntiRaccolta] = 1;
                                        }
                                    }
                                }
                            });
                        });
                        for (var d in daymap) {
                            var n = daymap[d];
                            n.text = toMessage(n.text);
                            if (n.text) {
                                //console.log('scheduling ' + n.id + ' at ' + n.firstAt);
                                notifArray.push(n);
                                //break;
                            }
                        }
                    }
                });
                console.log('Scheduled ' + notifArray.length);
                if (notifArray.length > 0) {
                    notifArray.sort(function(a,b) {
                        return a.firstAt.getTime() - b.firstAt.getTime();
                    });
                    if (ionic.Platform.isIOS() && notifArray.length > 64) {
                        notifArray = notifArray.slice(0, 63);
                    }
                    // notifArray = [notifArray[0]];
                    console.log('Starting from ' + notifArray[0].firstAt);
                    notifArray.push(
                        {
                            id: id,
                            title: $filter("translate")("notif_warning"),
                            text: $filter("translate")("notif_warning_text"),
                            smallIcon: 'res://drawable-hdpi/notification.png',
                            prio: 'high',
                            // autoCancel: true,
                            firstAt: notifArray[notifArray.length - 1].firstAt.getTime() + 60*1000
                        }
                    );
                    if (cordova && cordova.plugins && cordova.plugins.notification) cordova.plugins.notification.local.schedule(notifArray);
                }
            });
        }
    };

    var save = function () {
        $rootScope.profili.forEach(function (p) {
            buildAree(p);
            buildPaP(p);
            buildSettings(p);
        });
        localStorage.profiles = JSON.stringify($rootScope.profili);

        // update
        ProfiliFactory.read();
        var profileIndex = -1;
        if ($rootScope.selectedProfile) {
            $rootScope.profili.forEach(function (profile, pi) {
                if (profile.name == $rootScope.selectedProfile.name) profileIndex = pi;
            });
        } else {
            profileIndex = 0;
        }
        DataManager.updateProfiles($rootScope.profili);
        ProfiliFactory.updateNotifications();
        ProfiliFactory.select(profileIndex);
    };

    var indexof = function (id) {
        for (var pi = 0; pi < $rootScope.profili.length; pi++) {
            if ($rootScope.profili[pi].id == id) return pi;
        }
        return -1;
    };

    var byname = function (profileName) {
        for (var pi = 0; pi < $rootScope.profili.length; pi++) {
            if ($rootScope.profili[pi].name == profileName) return $rootScope.profili[pi];
        }
        return null;
    };

    ProfiliFactory.read = function () {
        if (localStorage.profiles) {
            if (localStorage.profiles.charAt(0) == '[') {
                $rootScope.profili = JSON.parse(localStorage.profiles);
                $rootScope.profili.forEach(function (profile) {
                    profile.image = "img/rifiuti_btn_radio_off_holo_dark.png";
                });
            } else {
                localStorage.removeItem('profiles');
            }
        }
    };

    ProfiliFactory.select = function (profileIndex) {
        if (!!$rootScope.selectedProfile) {
            var p = ProfiliFactory.byId($rootScope.selectedProfile.id);
            if (p) p.image = "img/rifiuti_btn_radio_off_holo_dark.png";
            $rootScope.selectedProfile = null;
        }
        if (profileIndex != -1) {
            $rootScope.profili[profileIndex].image = "img/rifiuti_btn_radio_on_holo_dark.png";
            $rootScope.selectedProfile = $rootScope.profili[profileIndex];
            localStorage.selectedProfileId = $rootScope.selectedProfile.id;
            Raccolta.aree().then(function (myAree) {
                //console.log('selectedProfile: '+JSON.stringify($rootScope.selectedProfile.name));
            });
        }
    };

    ProfiliFactory.byId = function (id) {
        for (var pi = 0; pi < $rootScope.profili.length; pi++) {
            if ($rootScope.profili[pi].id == id) return $rootScope.profili[pi];
        }
        return null;
    };

    var readNotes = function () {
        var notes = localStorage.notes;
        if (!!notes && notes.charAt(0) == '{') notes = JSON.parse(localStorage.notes);
        else notes = {};
        return notes;
    };

    var saveNotes = function (notes) {
        localStorage.notes = JSON.stringify(notes);
    }

    var treeWalkUp = function (tree, parentName, key, results) {
        if (!parentName || parentName == "") return;
        tree.forEach(function (node) {
            if (node['nome'] == parentName && results.indexOf(node[key]) < 0) {
                //var utenzaOK = node.utenza[$rootScope.selectedProfile.utenza.tipologiaUtenza];
                //if (utenzaOK) {
                results.push(node[key]);
                //}
                treeWalkUp(tree, node.parent, key, results);
            }
        });
    };

    var buildAree = function (p) {
        var myAree = [];
        var myGestori = [];
        var myIstituzioni = [];
        var areeList = DataManager.getSync('aree');
        areeList.forEach(function (area, ai, dbAree) {
            if (area.nome == p.area.nome) {
                var utenzaOK = area.utenza[p.utenza.tipologiaUtenza];
                if (utenzaOK) {
                    myAree.push(area.nome);
                    myIstituzioni.push(area.istituzione);
                    myGestori.push(area.gestore);
                }
                treeWalkUp(dbAree, area.parent, 'nome', myAree);
                treeWalkUp(dbAree, area.parent, 'istituzione', myIstituzioni);
                treeWalkUp(dbAree, area.parent, 'gestore', myGestori);
            }
        });
        p.aree = myAree;
        p.gestori = myGestori;
        p.istituzioni = myIstituzioni;
        //p.comuni=myComuni;
    };

    var buildPaP = function (p) {
        var data = DataManager.getSync('raccolta');
        var res = [];
        for (var i = 0; i < data.length; i++) {
            if (!!data[i].tipologiaPuntoRaccolta && Utili.isPaP(data[i].tipologiaPuntoRaccolta) &&
                data[i].tipologiaUtenza == p.utenza.tipologiaUtenza && p.aree.indexOf(data[i].area) >= 0) {
                if (res.indexOf(data[i].tipologiaPuntoRaccolta) < 0) res.push(data[i].tipologiaPuntoRaccolta);
            }
        }
        p.PaP = res;
    }

    var buildSettings = function (p) {
        if (!p.settings) {
            // init
            p.settings = {
                enableNotifications: true,
                papTypes: {},
                notificationsTime: 54000
            };

            for (var i = 0; i < p.PaP.length; i++) {
                var pap = p.PaP[i];
                p.settings.papTypes[pap] = true;
            }
        } else {
            // update missing PaPs
            for (var i = 0; i < p.PaP.length; i++) {
                var pap = p.PaP[i];
                if (!p.settings.papTypes[pap]) {
                    p.settings.papTypes[pap] = true;
                }
            }
        }
    };

    ProfiliFactory.tipidiutenza = function () {
        return DataManager.get('data/db/profili.json').then(function (results) {
            return results.data;
        });
    };

    ProfiliFactory.add = function (name, utenza, area) {
        if (!byname(name)) {
            var id = "" + new Date().getTime();
            var res = {
                id: id,
                name: name,
                utenza: utenza,
                area: area
            };
            $rootScope.profili.push(res);
            save();
            return res;
        }
        return null;
    };

    ProfiliFactory.update = function (id, name, utenza, area) {
        var old = byname(name);
        if (!old || old.id == id) {
            old = this.byId(id);
            old.name = name;
            old.area = area;
            old.utenza = utenza;
            save();
            return old;
        }
        return null;
    };

    ProfiliFactory.saveAll = function () {
        localStorage.profiles = JSON.stringify($rootScope.profili);
        ProfiliFactory.updateNotifications();
    };

    ProfiliFactory.remove = function (id) {
        var v = $rootScope.profili;
        v.splice(indexof(id), 1);
        $rootScope.profili = v;
        save();
    };

    ProfiliFactory.selectedProfileIndex = function () {
        return indexof(localStorage.selectedProfileId);
    };

    /*NOTES*/
    ProfiliFactory.getNotes = function () {
        return readNotes()[localStorage.selectedProfileId];
    };

    ProfiliFactory.addNote = function (txt) {
        var allNotes = readNotes();
        var notes = allNotes[localStorage.selectedProfileId];
        if (!notes) notes = [];
        notes.push(txt);
        allNotes[localStorage.selectedProfileId] = notes;
        saveNotes(allNotes);
        return notes;
    };

    ProfiliFactory.updateNote = function (idx, txt) {
        var allNotes = readNotes();
        var notes = allNotes[localStorage.selectedProfileId];
        if (!notes && !!notes[idx]) {
            return null
        };
        notes[idx] = txt;
        saveNotes(allNotes);
        return notes;
    };

    ProfiliFactory.deleteNotes = function (idx) {
        var allNotes = readNotes();
        var notes = allNotes[localStorage.selectedProfileId];
        var newNotes = [];
        for (var i = 0; i < notes.length; i++) {
            if (idx.indexOf(i) < 0) {
                newNotes.push(notes[i])
            };
        }
        allNotes[localStorage.selectedProfileId] = newNotes;
        saveNotes(allNotes);
        return newNotes;
    };

    return ProfiliFactory;
});
