angular.module('rifiuti.services.data', [])

.factory('DataManager', function ($http, $q, Utili, $rootScope) {
    //var ENDPOINT_URL = 'https://tn.smartcommunitylab.it/riciclo';
    var ENDPOINT_URL = 'http://localhost:8001/riciclo';
    // TODO handle
    //var USE_DRAFT = true;

    var LOCAL_DATA_URL = 'data/data.zip';
    var VERSION_URL = ENDPOINT_URL + '/appDescriptor/' + APP_ID;

    //var LANG = 'IT';
    //var COMUNI_LIST = ['022003','022034'];

    var completeDataPrefix = APP_ID+"_completeData";
    var categorieMapPrefix = APP_ID+"_categorieMap";
    var globalSettingsPrefix = APP_ID+"_globalSettings";
    var profileDataPrefix = APP_ID+"_profileData";
    var profilesPrefix = APP_ID+"_profiles";
    var selectedProfileIdPrefix = APP_ID+"_selectedProfileId";
    var tutorialPrefix = APP_ID+"_tutorial";
    var versionPrefix = APP_ID+"_version";
    var isDevModePrefix = APP_ID+"_isDevMode";
    var colorCodeMapPrefix = APP_ID+"_colorCodeMap";

    var dataURL = LOCAL_DATA_URL;

    var completeData = null;
    var profili = null;
    var profileData = null;
    var categorieMap = null;
    var colorCodeMap = null;

    var errorHandler = function (e) {
        console.log(e);
    };

    // generate specific data mapped onto user types
    var preprocess = function (data) {
            var colorMap = {};
            data.raccolta.forEach(function (r) {
                var key = r.tipologiaUtenza + '--' + r.tipologiaPuntoRaccolta;
                if (!colorMap[key]) {
                    colorMap[key] = {};
                }
                // TODO! correct the multiple colors case
                //      colorMap[key][r.tipologiaRaccolta] = r.colore;
                colorMap[key][r.area] = r.colore;
            });

            var tipologiePuntiRaccolta = {};
            data.categorie.tipologiaPuntiRaccolta.forEach(function (tpr) {
                tipologiePuntiRaccolta[tpr.id] = tpr;
            });

            var prMap = {};
            var prCalMap = {};
            var newPuntiRaccolta = {};
            data.puntiRaccolta.forEach(function (pr) {
                for (var i = 0; i < pr.utenzaArea.length; i++) {
                    var ua = pr.utenzaArea[i];
                    ua.colore = colorMap[ua.tipologiaUtenza + '--' + pr.tipologiaPuntiRaccolta];
                }

                pr.tipoPuntoRaccolta = tipologiePuntiRaccolta[pr.tipologiaPuntiRaccolta];
            });
            data.raccolta.forEach(function (racc) {
                racc.tipoPuntoRaccolta = tipologiePuntiRaccolta[racc.tipologiaPuntoRaccolta];
            });
            return data;
        }
        // limit the data to the necessary one only
    var updateProfileData = function () {
        profileData = {};
        if (completeData == null) {
            completeData = JSON.parse(localStorage[completeDataPrefix]);
        }
        profileData.aree = completeData.aree;
        profileData.gestori = completeData.gestori;
        profileData.istituzioni = completeData.istituzioni;
        profileData.tipologiaProfilo = completeData.tipologiaProfilo;
        profileData.raccolta = completeData.raccolta;
        profileData.riciclabolario = completeData.riciclabolario;
        profileData.categorie = completeData.categorie;
        profileData.segnalazione = completeData.segnalazione;

        $rootScope.isDevMode = getIsDevMode();

        colorCodeMap = {};
        completeData.colore.forEach(function (colorCode) {
            colorCodeMap[colorCode.nome] = colorCode.codice;
        });

        localStorage[colorCodeMapPrefix] = JSON.stringify(colorCodeMap);

        categorieMap = {};
        if(profileData.categorie){
            var utenzaMap = {};
            tipoUtenza = profileData.categorie.tipologiaUtenza;
            tipoUtenza.forEach(function (utenza){
                utenzaMap[utenza.id] = utenza;
            })
            categorieMap['tipologiaUtenza'] = utenzaMap;

            var rifiutoMap = {};
            tipoRifiuto = profileData.categorie.tipologiaRifiuto;
            tipoRifiuto.forEach(function (rifiuto){
                rifiutoMap[rifiuto.id] = rifiuto;
            })
            categorieMap['tipologiaRifiuto'] = rifiutoMap;

            var tipoRaccoltaMap = {};
            tipoRaccolta = profileData.categorie.tipologiaRaccolta;
            tipoRaccolta.forEach(function (raccolta){
                tipoRaccoltaMap[raccolta.id] = raccolta;
            })
            categorieMap['tipologiaRaccolta'] = tipoRaccoltaMap;

            var puntiRaccoltaMap = {};
            tipoPuntiRaccolta = profileData.categorie.tipologiaPuntiRaccolta;
            tipoPuntiRaccolta.forEach(function (punto){
                puntiRaccoltaMap[punto.id] = punto;
            })
            categorieMap['tipologiaPuntiRaccolta'] = puntiRaccoltaMap;
        }

        localStorage[categorieMapPrefix] = JSON.stringify(categorieMap);

        if (profili) {
            var map = {};
            profili.forEach(function (p) {
                profileData['puntiRaccolta_' + p.utenza.tipologiaUtenza] = [];
                profileData['puntiRaccoltaCalendar_' + p.utenza.tipologiaUtenza] = [];
            });
            profili.forEach(function (p) {
                completeData.puntiRaccolta.forEach(function (pr) {
                    for (var i = 0; i < pr.utenzaArea.length; i++) {
                        var ua = pr.utenzaArea[i];
                        if (ua.tipologiaUtenza !== p.utenza.tipologiaUtenza) continue;

                        var prKey = pr.tipologiaPuntiRaccolta + ' ' + (pr.dettagliZona ? pr.dettagliZona : ua.area);

                        if (Utili.belongsTo(pr, ua.area, p) && !map[prKey + ua.tipologiaUtenza]) {
                            var newPr = angular.copy(pr);
                            delete newPr.utenzaArea;
                            newPr.area = ua.area;
                            newPr.tipologiaUtenza = ua.tipologiaUtenza;
                            newPr.colore = ua.colore;
                            profileData['puntiRaccolta_' + ua.tipologiaUtenza].push(newPr);
                            map[prKey + ua.tipologiaUtenza] = true;

                            if (newPr.orarioApertura) {
                                newPr.orarioApertura.forEach(function (cal) {
                                    Utili.expandOrarioApertura(cal)
                                });
                                profileData['puntiRaccoltaCalendar_' + ua.tipologiaUtenza].push(newPr);
                            }
                        }
                    }
                });
                //        completeData['puntiRaccolta_'+p.utenza.tipologiaUtenza].forEach(function(pr) {
                //          if (Utili.belongsTo(pr,p) && profileData['puntiRaccolta_'+p.utenza.tipologiaUtenza].indexOf(pr) == -1) {
                //            if (pr.orarioApertura) pr.orarioApertura.forEach(function(cal){Utili.expandOrarioApertura(cal)});
                //            profileData['puntiRaccolta_'+p.utenza.tipologiaUtenza].push(pr);
                //          }
                //        });
                //        if (completeData['puntiRaccoltaCalendar_'+p.utenza.tipologiaUtenza]) {
                //          completeData['puntiRaccoltaCalendar_'+p.utenza.tipologiaUtenza].forEach(function(pr) {
                //            if (Utili.belongsTo(pr,p) && profileData['puntiRaccoltaCalendar_'+p.utenza.tipologiaUtenza].indexOf(pr) == -1)
                //            {
                //              if (pr.orarioApertura) pr.orarioApertura.forEach(function(cal){Utili.expandOrarioApertura(cal)});
                //              profileData['puntiRaccoltaCalendar_'+p.utenza.tipologiaUtenza].push(pr);
                //            }
                //          });
                //        }
            });
        }


        localStorage[profileDataPrefix] = JSON.stringify(profileData);
    };

    var process = function (url) {
        var deferred = $q.defer();
        JSZipUtils.getBinaryContent(url, function (err, data) {
            if (err) {
                deferred.reject(false); // or handle err
            }
            var zip = new JSZip(data);
            var jsons = zip.filter(function (relativePath, file) {
                return relativePath.indexOf('.json') > 0;
            });
            if (jsons.length > 0) {
                var str = jsons[0].asText();
                var rifiutiObj = angular.fromJson(str.trim());
                completeData = preprocess(rifiutiObj);
                localStorage[completeDataPrefix] = JSON.stringify(completeData);
                updateProfileData();
                deferred.resolve(true);
            }
        });
        return deferred.promise;
    };

    var get = function (url) {
        var deferred = $q.defer();
        if (profileData == null) {
            if (localStorage[profileDataPrefix]) {
                profileData = JSON.parse(localStorage[profileDataPrefix]);
            } else if (dataURL) {
                process(dataURL).then(function (res) {
                    get(url).then(function (results) {
                        deferred.resolve(results);
                    });
                });
                return deferred.promise;
            } else {
                deferred.reject('no data');
                return deferred.promise;
            }
        }
        if (url === 'data/db/aree.json') {
            deferred.resolve({
                data: profileData.aree
            });
        } else if (url === 'data/db/gestori.json') {
            deferred.resolve({
                data: profileData.gestori
            });
        } else if (url === 'data/db/istituzioni.json') {
            deferred.resolve({
                data: profileData.istituzioni
            });
        } else if (url === 'data/db/puntiRaccolta.json') {
            deferred.resolve({
                data: profileData.puntiRaccolta
            });
        } else if (url.indexOf('data/db/puntiRaccolta_') == 0) {
            deferred.resolve({
                data: profileData[url.substr(8, url.length - 13)]
            });
        } else if (url === 'data/db/puntiRaccoltaCalendar.json') {
            deferred.resolve({
                data: profileData.puntiRaccoltaCalendar
            });
        } else if (url.indexOf('data/db/puntiRaccoltaCalendar_') == 0) {
            deferred.resolve({
                data: profileData[url.substr(8, url.length - 13)]
            });
        } else if (url === 'data/db/raccolta.json') {
            deferred.resolve({
                data: profileData.raccolta
            });
        } else if (url === 'data/db/profili.json') {
            deferred.resolve({
                data: profileData.tipologiaProfilo
            });
        } else if (url === 'data/db/riciclabolario.json') {
            deferred.resolve({
                data: profileData.riciclabolario
            });
        } else if (url === 'data/db/tipologiaRifiuto.json') {
            deferred.resolve({
                data: profileData.categorie.tipologiaRifiuto
            });
        } else if (url === 'data/db/segnalazioni.json') {
            deferred.resolve({
                data: profileData.segnalazione
            });
        } else if (url === 'data/db/tipologiaUtenza.json') {
            deferred.resolve({
                data: profileData.categorie.tipologiaUtenza
            });
        } else if (url === 'data/db/tipologiaRifiuto.json') {
            deferred.resolve({
                data: profileData.categorie.tipologiaRifiuto
            });
        } else if (url === 'data/db/tipologiaRaccolta.json') {
            deferred.resolve({
                data: profileData.categorie.tipologiaRaccolta
            });
        } else if (url === 'data/db/categoria/tipologiaPuntiRaccolta.json') {
            deferred.resolve({
                data: profileData.categorie.tipologiaPuntiRaccolta
            });
        } else {
            console.log('USING OLD FILE! ' + url);
            $http.get(url).then(function (results) {
                deferred.resolve(results);
            });
        }

        return deferred.promise;
    };

    var saveLang = function () {
        localStorage[globalSettingsPrefix] = JSON.stringify($rootScope.globalSettings);

        //var deferred = $q.defer();
        process(getDataURL(true));

        localStorage[globalSettingsPrefix] = JSON.stringify($rootScope.globalSettings);
    };

    var saveDraft = function () {
        localStorage[globalSettingsPrefix] = JSON.stringify($rootScope.globalSettings);

        //var deferred = $q.defer();
        process(getDataURL(true));

        localStorage[globalSettingsPrefix] = JSON.stringify($rootScope.globalSettings);
    };

    var getGlobalSettings = function () {
        if ($rootScope.globalSettings){
            return $rootScope.globalSettings;
        }else{
            if(localStorage[globalSettingsPrefix]){
                return JSON.parse(localStorage[globalSettingsPrefix]);
            }

            return null;
        }
    }

    var getProfiles = function () {
        if (localStorage[profilesPrefix]){
            return JSON.parse(localStorage[profilesPrefix]);
        }

        return null;
    }

    var saveProfiles = function(profiles){
        localStorage[profilesPrefix] = JSON.stringify(profiles);
    }

    var getIsDevMode = function () {
        if ($rootScope.isDevMode==null){
            if(localStorage[isDevModePrefix]!=null){
                $rootScope.isDevMode = JSON.parse(localStorage[isDevModePrefix]);
                return JSON.parse(localStorage[isDevModePrefix]);
            }

            $rootScope.isDevMode = false;
        }

        return $rootScope.isDevMode;
    }

    var getColorById = function(colore){
      var localColorCodeMap = {};
      if(!!colorCodeMap){
        localColorCodeMap = colorCodeMap;
      }else{
        if(localStorage[colorCodeMapPrefix]){
          colorCodeMap = JSON.parse(localStorage[colorCodeMapPrefix]);
          localColorCodeMap = colorCodeMap;
        }else{
            return 'grey';
        }
      }

      if(localColorCodeMap[colore]){
        return localColorCodeMap[colore];
      }

      if (colore in ICON_COLOR_MAP) return ICON_COLOR_MAP[colore];
      return 'grey';
    }


    var getIconById = function(tipologia){
      var tipoPuntoRaccolta = getCategoriaById('tipologiaPuntiRaccolta',tipologia);

      if(!!tipoPuntoRaccolta && tipoPuntoRaccolta['icona']){
        return tipoPuntoRaccolta['icona'];
      }

      if (tipologia in ICON_POINT_MAP) return ICON_POINT_MAP[tipologia];
      return null;
    }

    var saveIsDevMode = function(isDevMode){
        $rootScope.isDevMode = isDevMode;
        localStorage[isDevModePrefix] = JSON.stringify(isDevMode);
    }

    var getTutorial = function () {
        if (localStorage[tutorialPrefix]){
            return localStorage.getItem(tutorialPrefix);
        }

        return null;
    }

    var saveTutorial = function (saveData){
        localStorage.setItem(tutorialPrefix, "false");
    }

    var getCategoriaById = function (categoria, id) {
       if (categorieMap == null) {
            if (localStorage[categorieMapPrefix]) {
                categorieMap = JSON.parse(localStorage[categorieMapPrefix]);
            }
        }

       var mapToInspect = {};
       var dataUrl = null;
       if (categoria === 'tipologiaUtenza') {
           mapToInspect = categorieMap.tipologiaUtenza;
        } else if (categoria === 'tipologiaRifiuto') {
           mapToInspect = categorieMap.tipologiaRifiuto;
        } else if (categoria === 'tipologiaRaccolta') {
           mapToInspect = categorieMap.tipologiaRaccolta;
        } else if (categoria === 'tipologiaPuntiRaccolta') {
           mapToInspect = categorieMap.tipologiaPuntiRaccolta;
        }

        var returnItem = null;
        for (key in mapToInspect) {
            var item = mapToInspect[key];
            if(item.id == id){
                returnItem = item;
            }
        }

        return returnItem;
    }


    var getDataURL = function (remote) {
        if (remote) {
            if (getDraftEnabled()) {
                //return ENDPOINT_URL + '/draft/' + APP_ID + '/zip';
                return ENDPOINT_URL + '/zip/' + APP_ID + '?lang=' + getSelectedLang() + '&draft=true' + getComuniString();
            } else {
                //http://localhost:8000/riciclo/zip/TRENTO?lang=it&draft=true&comune[]=022205
                return ENDPOINT_URL + '/zip/' + APP_ID + '?lang=' + getSelectedLang() + '&draft=false' + getComuniString();
            }
        } else {
            return LOCAL_DATA_URL;
        }
    }

    var getSelectedLang = function(){
        if($rootScope.globalSettings.selectedLang){
            return $rootScope.globalSettings.selectedLang;
        }else{
            return LANG[0];
        }
    }

    var getDraftEnabled = function(){
        if($rootScope.globalSettings.draftEnabled!=null){
            return $rootScope.globalSettings.draftEnabled;
        }else{
            return USE_DRAFT;
        }
    }

    var getComuniString = function(){
        var comuniString = '';

        for (var i = 0; i < COMUNI_LIST.length; i++) {
            comuniString += '&comune[]='+COMUNI_LIST[i];
        }

        return comuniString;
    }

    var doWithVersion = function (deferred, v, remote) {
        var storedVersion = null;
        if (localStorage) {
            storedVersion = localStorage[versionPrefix];
        } else {
            storedVersion = v;
        }
        if (!storedVersion) storedVersion = DATA_VERSION;

        if (storedVersion && storedVersion >= v) {
            deferred.resolve(true);
            return;
        };

        process(getDataURL(remote)).then(function (result) {
            if (result) {
                localStorage[versionPrefix] = v
            };
            deferred.resolve(true);
        });
    }

    var extractVersion = function (data) {
        var obj = data;

        if (!obj) return null;
        if (USE_DRAFT) return obj.draftState ? obj.draftState.version : null;
        return obj.publishState ? obj.publishState.version : null;
    };

    return {
        get: get,
        getCategoriaById: getCategoriaById,
        saveLang: saveLang,
        getSelectedLang: getSelectedLang,
        saveDraft: saveDraft,
        getDraftEnabled: getDraftEnabled,
        getGlobalSettings: getGlobalSettings,
        getProfiles: getProfiles,
        saveProfiles: saveProfiles,
        getTutorial: getTutorial,
        saveTutorial: saveTutorial,
        getIsDevMode: getIsDevMode,
        saveIsDevMode: saveIsDevMode,
        getColorById: getColorById,
        getIconById: getIconById,
        updateProfiles: function (newProfiles) {
            profili = newProfiles;
            updateProfileData();
        },
        checkVersion: function (currentProfiles) {
            var deferred = $q.defer();
            profili = currentProfiles;
            $http.get(VERSION_URL)
                .success(function (data) {
                    var version = extractVersion(data);
                    if (version) {
                        doWithVersion(deferred, version, true);
                    } else {
                        doWithVersion(deferred, DATA_VERSION);
                    }
                })
                .error(function (e) {
                    doWithVersion(deferred, DATA_VERSION);
                });
            return deferred.promise;
        },
        reset: function () {
            // TODO
            // - clean profileData, objectData, version
            // - check version
        },
        getSync: function (key) {
            return profileData[key];
        }
    };
});
