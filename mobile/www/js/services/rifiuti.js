angular.module('rifiuti.services.rifiuti', [])

.factory('Raccolta', function (DataManager, $rootScope, $q, GeoLocate, Utili) {

  return {
    aree: function() {
      return DataManager.get('data/db/aree.json').then(function (results) {
        if ($rootScope.selectedProfile) {
          return $rootScope.selectedProfile.aree;
        }
        return [];
      });
    },
    puntiraccolta: function(options) {
      var ptdeferred = $q.defer();
      this.aree().then(function(myAree){
        DataManager.get('data/db/puntiRaccolta_'+$rootScope.selectedProfile.utenza.tipologiaUtenza+'.json').then(function (results) {
          var myPunti=[];
          var myPuntiDone=[];
          if ($rootScope.selectedProfile) {
            results.data.forEach(function(punto,pi,dbPunti){
              if (options &&
                  (options.zona && punto.dettagliZona!=options.zona ||
                   options.tipo && punto.tipologiaPuntiRaccolta!=options.tipo)) return;

              if (/*punto.dettagliZona && */Utili.belongsTo(punto, punto.area, $rootScope.selectedProfile)) {
                  if (myPuntiDone.indexOf(punto.dettagliZona)==-1) {
                    myPunti.push(punto);
                    if (!options || !options.all) myPuntiDone.push(punto.dettagliZona);
                }
              }
            });
          }
          myPunti.forEach(function(punto){
            if (punto.localizzazione) {
              GeoLocate.distanceTo(punto.localizzazione.split(',')).then(function (distance) {
                //console.log('distance: ' + distance);
                punto['distance'] = distance;
              });
            } else {
              console.log('invalid location for: '+punto.dettagliZona);
            }
          });
          
//          $cordovaGeolocation.getCurrentPosition({ timeout:10000,enableHighAccuracy:true }).then(function (position) {
//            position={ coords:{ latitude:'46.0', longitude:'11.0' } };
//
//            if (position.coords && position.coords.latitude && position.coords.longitude) {
//              myPunti.forEach(function(punto){
//                if (punto.localizzazione) {
//                  var distance=$rootScope.distance([position.coords.latitude,position.coords.longitude],punto.localizzazione.split(','));
//                  punto['distance']=distance;
//                } else {
//                  console.log('invalid location for: '+punto.dettagliZona);
//                }
//              });
//            } else {
//              console.log('invalid pos: '+JSON.stringify(position));
//            }
//          }, function(err) {
//            console.log('cannot geolocate!');
//          });
          
          ptdeferred.resolve(myPunti);
        });
      });
      return ptdeferred.promise;
    },
    raccolta: function(options) {
      var deferred = $q.defer();
      this.aree().then(function(myAree){
        DataManager.get('data/db/raccolta.json').then(function (results) {
          var myRaccolta=[];
          if ($rootScope.selectedProfile) {
            results.data.forEach(function(regola,ri,dbRaccolta){
              if (options && options.tipo && regola.tipologiaRaccolta!=options.tipo) return;

              if (options && options.tiporifiuto && regola.tipologiaRifiuto!=options.tiporifiuto) return;
              if (options && options.tipirifiuto && options.tipirifiuto.indexOf(regola.tipologiaRifiuto)==-1) return;

              if (options && options.tipopunto && regola.tipologiaPuntoRaccolta!=options.tipopunto) return;
              if (options && options.tipipunto && options.tipipunto.indexOf(regola.tipologiaPuntoRaccolta)==-1) return;

              if ($rootScope.selectedProfile.aree.indexOf(regola.area)!=-1 &&
                  regola.tipologiaUtenza==$rootScope.selectedProfile.utenza.tipologiaUtenza) {
                var icona = Utili.iconFromRegola(regola);
                if (icona) regola['icon'] = icona;
                myRaccolta.push(regola);
              }
            });
          }
          deferred.resolve(myRaccolta);
        });
      });
      return deferred.promise;
    },
    rifiuto: function(nome) {
      return this.rifiuti().then(function(myRifiuti){
        var myRifiuto=null;
        myRifiuti.forEach(function(rifiuto){
          if (rifiuto.nome==nome) myRifiuto=rifiuto;
        });
        return myRifiuto;
      });
    },
    rifiuti: function(options) {
      var deferred = $q.defer();
      this.aree().then(function(myAree){
        DataManager.get('data/db/riciclabolario.json').then(function (results) {
//          var myTipologie=[];
          var myRifiuti=[];
          if ($rootScope.selectedProfile) {
            results.data.forEach(function(rifiuto,ri,dbRifiuti){
              if (options && options.tipo && rifiuto.tipologiaRifiuto!=options.tipo) return;
              if (options && options.tipi && options.tipi.indexOf(rifiuto.tipologiaRifiuto)==-1) return;
              if ($rootScope.selectedProfile.aree.indexOf(rifiuto.area)!=-1 && rifiuto.tipologiaUtenza==$rootScope.selectedProfile.utenza.tipologiaUtenza) {
                myRifiuti.push(rifiuto);
//                if (myTipologie.indexOf(rifiuto.tipologiaRifiuto)==-1) myTipologie.push(rifiuto.tipologiaRifiuto);
              }
            });
            myRifiuti = myRifiuti.sort(function(a,b){return a.nome.localeCompare(b.nome)});
//            $rootScope.selectedProfile.rifiuti=myRifiuti;
//            $rootScope.selectedProfile.tipologie=myTipologie.sort();
          }
          deferred.resolve(myRifiuti);
        })
      });
      return deferred.promise;
    },
    contatti: function() {
      var deferred = $q.defer();
      DataManager.get('data/db/istituzioni.json').then(function (results) {
        var data=[];
        for (var i = 0; i < results.data.length; i++) {
            if ($rootScope.selectedProfile.istituzioni.indexOf(results.data[i].nome) >= 0) {
                data.push(results.data[i]);
            }
        }
        DataManager.get('data/db/gestori.json').then(function (gest) {
          for (var i = 0; i < gest.data.length; i++) {
            if ($rootScope.selectedProfile.gestori.indexOf(gest.data[i].ragioneSociale) >= 0) {
                data.push(gest.data[i]);
            }
          }
          deferred.resolve(data);    
        });
     });
      return deferred.promise;    
    },
    puntiRaccoltaCalendar : function(utenza, aree) {
      var deferred = $q.defer();
      if (!aree) aree = [];
      DataManager.get('data/db/puntiRaccoltaCalendar_'+utenza+'.json').then(function (results) {
        var data = results.data;
        var filtered = [];
        for (var i = 0; i < data.length; i++) {
          if (aree.indexOf(data[i].area)>=0) {
            var copy = null;
            for(var j = 0; j < aree.length; j++) {
              if (aree[j] in data[i].colore) {
                copy = angular.copy(data[i]);
                copy.colore = data[i].colore[aree[j]];
                break;
              }
            }
            if (copy != null) filtered.push(copy);
          }
        }
        deferred.resolve(filtered);
      });
      return deferred.promise;    
    },
    notificationCalendar : function(profilo) {
      var deferred = $q.defer();
      if (!profilo.aree) profilo.aree = [];
      DataManager.get('data/db/puntiRaccoltaCalendar_'+profilo.utenza.tipologiaUtenza+'.json').then(function (results) {
        var data = results.data;
        var filtered = [];
        for (var i = 0; i < data.length; i++) {
          if (profilo.aree.indexOf(data[i].area)>=0 &&
              Utili.isPaP(data[i].tipoPuntoRaccolta) &&
              (!profilo.settings.papTypes || profilo.settings.papTypes[data[i].tipologiaPuntiRaccolta]))
          {
            filtered.push({
              id: profilo.id,
              comune: profilo.area.etichetta,
              orarioApertura: data[i].orarioApertura,
              tipologiaPuntiRaccolta: data[i].tipologiaPuntiRaccolta,
              ora: profilo.settings.notificationsTime
            });
          }
        }
        deferred.resolve(filtered);
      });
      return deferred.promise;
    },
    areeForTipoUtenza: function(profile) {
      var deferred = $q.defer();
      DataManager.get('data/db/aree.json').then(function (results) {
        var data=results.data;
        var res = [];
        for (var i =0; i < data.length; i++) {
            var a = data[i];
            if (!!a.etichetta) {
              if (a.utenza[profile])
                {
                    res.push(a);
                }
            }
        }
        res.sort(function(a,b) {
            return a.etichetta.localeCompare(b.etichetta);
        });  
        deferred.resolve(res);  
      });
      return deferred.promise;    
    },
    tipiDiRaccolta: function(utenza, aree) {
      var deferred = $q.defer();
      DataManager.get('data/db/raccolta.json').then(function (results) {
        var data=results.data;
        var res = {};
        for (var i =0; i < data.length; i++) {
          if (data[i].tipologiaUtenza == utenza && aree.indexOf(data[i].area)>=0 && !!data[i].tipologiaRaccolta) {
            var list = res[data[i].tipologiaRaccolta];
            if (!list) {
              list = {};
              res[data[i].tipologiaRaccolta] = list;
            }
            list[data[i].tipologiaPuntoRaccolta+'_'+data[i].colore] = {
              tipologiaPuntoRaccolta: data[i].tipologiaPuntoRaccolta,
              colore: data[i].colore
            };
          }
        }
        deferred.resolve(res);  
      });
      return deferred.promise;    
    },
    segnalazioni: function(aree) {
      var deferred = $q.defer();
      DataManager.get('data/db/segnalazioni.json').then(function(results) {
        var data = results.data;
        var res = [];
          for (var i = 0; i < data.length;i++) {
            if (aree.indexOf(data[i].area) >=0) {
                res.push(data[i]);
            }
          }
          deferred.resolve(res);
      });
      return deferred.promise;
    },
    hasSegnalazioni: function() {
      var signals = DataManager.getSync('segnalazione');
      if (signals != null && signals.length > 0) return true;
      return false;
    },
    immagini: function() {
      return DataManager.get('data/support/tipologiaRifiutoImmagini.json').then(function (results) {
        var imgs={};
        results.data.forEach(function(immagine,ii,dbImmagini){
          imgs[immagine.valore]=immagine.immagine;
        });
        $rootScope.immagini=imgs;
        return imgs;
      });
    },
    tipiDiRifiuti: function() {
      return DataManager.get('data/db/tipologiaRifiuto.json').then(function (results) {
        var tipiRifiutiArr={};
        results.data.forEach(function(tipoRifiuto,ii,dbRifiuto){
          tipiRifiutiArr[tipoRifiuto.id]=tipoRifiuto;
        });
        $rootScope.tipiDiRifiuti=tipiRifiutiArr;
        return tipiRifiutiArr;
      });
    },
    sortRaccolta: function(raccolta) {
            raccolta.sort(function(a,b){
            var aPaP = Utili.isPaP(a.tipoPuntoRaccolta);
            var bPaP = Utili.isPaP(b.tipoPuntoRaccolta);
            if (aPaP && !bPaP) return -1;
            if (bPaP && !aPaP) return 1;
            return a.tipologiaPuntoRaccolta.localeCompare(b.tipologiaPuntoRaccolta);
        });
    }
  }
})
