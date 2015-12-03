angular.module('rifiuti.controllers.raccolta', [])

.controller('tipidirifiutiCtrl', function ($scope, $rootScope, Raccolta) {
  $scope.match = function (query) {
    if (query.length < 3) {
      return function (item) {
        return false;
      }
    } else {
      return function (item) {
        return item.nome.toLowerCase().indexOf(query.toLowerCase()) != -1;
        //TODO: consentire la ricerca anche sulle traduzioni
      }
    }
  };
  
  $scope.rifiuti = [];
  $scope.tipiDiRifiuti = [];

  var init = function() {
    if ($scope.selectedProfile) {
      Raccolta.rifiuti().then(function(data){
        $scope.rifiuti = data;
          Raccolta.tipiDiRifiuti().then(function(data){
            $scope.tipiDiRifiuti = data;
              Raccolta.immagini().then(function(){
              var tipologieMap = {};
              var results=[], row=[], counter=-1;
              for (var i = 0; i < $scope.rifiuti.length; i++) {
                  var tipologiaId = $scope.rifiuti[i].tipologiaRifiuto;
                  var tipologiaLabel = $scope.tipiDiRifiuti[tipologiaId].nome;
                  var tipologiaIcona = $scope.tipiDiRifiuti[tipologiaId].icona;
                  if (!(tipologiaId in tipologieMap)) tipologieMap[tipologiaId] = {link: tipologiaId,label:tipologiaLabel, img:$scope.immagini[tipologiaIcona]};
              }
              var tipologieArray = [];
              for (var t in tipologieMap) {
                tipologieArray.push(tipologieMap[t]);
              }
              tipologieArray = tipologieArray.sort(function(a,b) {return a.label.localeCompare(b.label);});

              for (var i=0; i<tipologieArray.length; i++) {
                counter++;
                if (counter==3) {
                  counter=0;
                  results.push(row);
                  row=[];
                }
                row.push(tipologieArray[i]);
              };
              if (row.length>0) results.push(row);
              $scope.tipologie=results;
            });
        });
    });
  }
};
  
  $rootScope.$watch('selectedProfile',function(a,b) {
    if (b == null || a.id != b.id) {
      init();
    }
  }); 

  init();
  
})
  
.controller('PDRCtrl', function ($scope, $rootScope, $timeout, Raccolta, $location, $stateParams, Utili, DataManager, uiGmapIsReady) {

  $scope.profile = null;
  
  $rootScope.checkMap();  
  
  $scope.updateIMG = function () {
    $scope.variableIMG = $scope.mapView ? "riciclo-lista" : "riciclo-map";
  };

  $scope.openMarkerClick = function ($markerModel) {
    $location.url('/app/puntoDiRaccolta/' + $rootScope.addr2id($markerModel.model.id));
  };

  $scope.map = {
    center: CENTER,
    zoom: ZOOM,
    pan: false,
    draggable: 'true',
    bounds: {},
    options: {
      'streetViewControl': false,
      'zoomControl': true,
      'mapTypeControl': false,
      styles: [{
        featureType: "poi",
        elementType: "labels",
        stylers: [{
          visibility: "off"
          }]
        }]
    }
  };

  $scope.control = {};
  uiGmapIsReady.promise().then(function (maps) {
      var map1 = $scope.control.getGMap();
      fitBoundsByMap(map1);
  });

  var fitBoundsByMap = function (map) {
    //  Make an array of the LatLng's of the markers you want to show
    //var LatLngList = new Array (new google.maps.LatLng (46.0802019,11.0859357), new google.maps.LatLng (45.4262289,10.9224915));
    var LatLngList = getLatLngListFromMarkers();
    //  Create a new viewpoint bound
    var bounds = new google.maps.LatLngBounds();
    //  Go through each...
    for (var i = 0, LtLgLen = LatLngList.length; i < LtLgLen; i++) {
      //  And increase the bounds to take this point
      bounds.extend (LatLngList[i]);
    }
    //  Fit these bounds to the map
    map.fitBounds(bounds);
    var center = bounds.getCenter();
    map.setCenter(center);
    $scope.map.center = {
                latitude: center.lat(),
                longitude: center.lng()
    };

    $scope.map.bounds = {
          northeast: { latitude: bounds.getNorthEast().lat(),
                       longitude: bounds.getNorthEast().lng() },
          southwest: { latitude: bounds.getSouthWest().lat(),
                       longitude: bounds.getSouthWest().lng() }
        };

    if(map.zoom>=ZOOM){
        map.setZoom(ZOOM);
        $scope.map.zoom = ZOOM;
    }
  }


  $scope.click = function () {
    $scope.mapView = !$scope.mapView;
    $scope.updateIMG();
  };

  var getLatLngListFromMarkers = function(){
    var latLngList = [];
    $scope.markers.models.forEach(function(point){
        var latLng = new google.maps.LatLng (point.latitude,point.longitude);
        latLngList.push(latLng);
    });

    return latLngList;
  }


  var addToList = function (item, list) {
    for (var i = 0; i < list.length; i++) {
      if (list[i].tipologiaPuntoRaccolta == item.tipologiaPuntiRaccolta) {
        list[i].locs.push(item);
        return;
      }
    }

    var icona = DataManager.getIconById(item.tipologiaPuntiRaccolta);

    list.push({
      aperto: false,
      tipoPuntoRaccolta: item.tipoPuntoRaccolta,
      tipologiaPuntoRaccolta: item.tipologiaPuntiRaccolta,
      icon: Utili.icon(icona),
      locs: [item]
    });
  };
  
  var init = function() {
    
    $scope.mapView = true;
    $scope.id = $stateParams.id != '!' ? $rootScope.id2addr($stateParams.id) : null;
    $scope.list = [];
    $scope.variableIMG = "riciclo-lista";

    Raccolta.puntiraccolta().then(function(punti){
      var points = [];
      var list = [];
      punti.forEach(function(punto){
        if (!!punto.dettagliZona && ($scope.id == null || punto.dettagliZona == $scope.id)) {
        var icona = DataManager.getIconById(punto.tipologiaPuntiRaccolta);
        var icon = {
            url: Utili.poiIcon(icona),
            scaledSize: new google.maps.Size(45, 45)
          };
          points.push({
            id: punto.dettagliZona,
            latitude: punto.localizzazione.split(',')[0],
            longitude: punto.localizzazione.split(',')[1],
            icon: icon
          });
          addToList(punto, list);
        }
      });

      $scope.markers = {
        control: {},
        models: points,
        coords: 'self',
        fit: false,
        icon: 'icon',
        doCluster: points.length > 10,
        clusterOptions: {minimumClusterSize : 5}
      };
      if (list.length == 1) {
        list[0].aperto = true;
      }
      else {
          Raccolta.sortRaccolta(list);
      }

      $scope.list = list;
    });

  };


  $rootScope.$watch('selectedProfile',function(a,b) {
    if ((b == null || a.id != b.id) && a.id != $scope.profile) {
      $scope.profile = a.id;
      init();
    }
  }); 
  
  init();

})

.controller('TDRCtrl', function ($scope, $rootScope, DataManager, Raccolta, Utili) {
  $scope.icon = function(item) {
    var colorById = DataManager.getColorById(item.colore);
    var icona = DataManager.getIconById(item.tipologiaPuntoRaccolta);

    return Utili.icon(icona,colorById);
  };

  
  var init = function() {
    Raccolta.tipiDiRaccolta($rootScope.selectedProfile.utenza.tipologiaUtenza, $rootScope.selectedProfile.aree).then(function (data) {
      $scope.tipi = data;
    });
  };
  
  $rootScope.$watch('selectedProfile',function(a,b) {
    if ((b == null || a.id != b.id)) {
      init();
    }
  }); 
  
  init();

})

.controller('RaccoltaCtrl', function ($scope, $stateParams, Raccolta, Utili) {

  $scope.id = $stateParams.id;

  Raccolta.raccolta({ tipo:$scope.id }).then(function(raccolta){
    if(!$scope.nome){
        $scope.nome = raccolta[0].tipoRaccolta.nome;
    }
    if(!$scope.comeConferire){
        $scope.comeConferire = raccolta[0].tipoRaccolta.comeConferire;
    }
    if(!$scope.prestaAttenzione){
        $scope.prestaAttenzione = raccolta[0].tipoRaccolta.prestaAttenzione;
    }

    var tipirifiuto=[], tipipunto=[];
    raccolta.forEach(function(regola){
      if (tipirifiuto.indexOf(regola.tipologiaRifiuto)==-1) tipirifiuto.push(regola.tipologiaRifiuto);
      if (tipipunto.indexOf(regola.tipologiaPuntoRaccolta)==-1) tipipunto.push(regola.tipologiaPuntoRaccolta);
    });

    Raccolta.rifiuti({ tipi:tipirifiuto }).then(function(rifiuti){
      $scope.rifiuti=rifiuti;
    });

    var raccoltaMap = {};
    raccolta.forEach(function(item){
        if (raccoltaMap[item.tipologiaPuntoRaccolta] != null) {
            raccoltaMap[item.tipologiaPuntoRaccolta].infoRaccolta = '';
//            raccoltaMap[item.tipologiaPuntoRaccolta].tipologiaRifiuto += ', ' + item.tipologiaRifiuto;
            return;
        }
        var newItem = angular.copy(item);
        raccoltaMap[item.tipologiaPuntoRaccolta] = newItem;

        Raccolta.puntiraccolta({ tipo:newItem.tipologiaPuntoRaccolta }).then(function(punti){
          newItem['punti']=punti;
        });
    });
    var raccoltaList = [];
    for (var tpr in raccoltaMap) {
        raccoltaList.push(raccoltaMap[tpr]);
    }

    if (raccoltaList.length == 1) raccoltaList[0].aperto = true;
    else Raccolta.sortRaccolta(raccoltaList);
    $scope.raccolta=raccoltaList;

//    Raccolta.raccolta({ tipipunto:tipipunto, tipirifiuto:tipirifiuto }).then(function(raccolta){
//      var raccoltaMap = {};
//      raccolta.forEach(function(item){
//        Raccolta.puntiraccolta({ tipo:item.tipologiaPuntoRaccolta }).then(function(punti){
//          item['punti']=punti;
//        });
//      });
//      if (raccolta.length == 1) raccolta[0].aperto = true;
//      else {
//          Raccolta.sortRaccolta(raccolta);
//      }
//      $scope.raccolta=raccolta;
//    });
  });
})

.controller('RifiutiCtrl', function ($scope, $stateParams, $ionicConfig, $ionicHistory, Raccolta) {
  $scope.tipo = $stateParams.tipo;

  $scope.backButtonStyle = $ionicConfig.backButton.icon();

  Raccolta.raccolta({ tiporifiuto:$scope.tipo }).then(function(raccolta){
    raccolta.forEach(function(item){
      Raccolta.puntiraccolta({ tipo:item.tipologiaPuntoRaccolta }).then(function(punti){
        item['punti']=punti;
        if (!!punti[0]) {item['tipoPuntoRaccolta']=punti[0].tipoPuntoRaccolta;}
      });
    });
    if (raccolta.length == 1) raccolta[0].aperto = true;
    else {
          Raccolta.sortRaccolta(raccolta);
    }
    $scope.raccolta=raccolta;
  });

  Raccolta.rifiuti({ tipo:$scope.tipo }).then(function(rifiuti){
    $scope.rifiuti=rifiuti;
  });
})
.controller('RifiutoCtrl', function ($scope, $rootScope, $stateParams, Raccolta) {
  $scope.nome = $rootScope.id2addr($stateParams.nome);

  Raccolta.rifiuto($scope.nome).then(function(rifiuto){
    if (!rifiuto) return;
    Raccolta.raccolta({ tiporifiuto:rifiuto.tipologiaRifiuto }).then(function(raccolta){
      raccolta.forEach(function(item){
        Raccolta.puntiraccolta({ tipo:item.tipologiaPuntoRaccolta }).then(function(punti){
          item['punti']=punti;
        });
      });
      if (raccolta.length == 1) raccolta[0].aperto = true;
      else {
          Raccolta.sortRaccolta(raccolta);
      }
      $scope.raccolta=raccolta;
    });
  });
})

.controller('PuntoDiRaccoltaCtrl', function ($scope, $rootScope, $stateParams, $ionicNavBarDelegate, Raccolta, Utili, DataManager) {

  $scope.id = !!$stateParams.id && $stateParams.id != 'undefined' && $stateParams.id != 'null'? $rootScope.id2addr($stateParams.id) : null;
  $scope.pdr = {};
  $scope.orari = [];
  //[{giorno:"lunedì",orari:["12.00-14.00","15.30-17.30"...]}...]

  $scope.checkGiorni = function (item) {
    for (var j = 0; j < $scope.orari.length; j++) {
      if ($scope.orari[j].giorno == item) return j;
    }
    return -1;
  };
  
  $scope.clickNav = function() {
    if ($scope.pdr.localizzazione) window.open("http://maps.google.com?daddr="+$scope.pdr.localizzazione,"_system");
    else window.open("http://maps.google.com?daddr="+$scope.pdr.dettagliZona,"_system");
  };
  
  Raccolta.puntiraccolta({ zona:$scope.id, all:true }).then(function(punti){
    $scope.pdr = punti[0];
    punti.forEach(function(punto){
      punto.orarioApertura.forEach(function(orario) {
        var j = $scope.checkGiorni(orario.il);
        if (j == -1) {
          $scope.orari.push({
            giorno: orario.il.split(' '),
            ecceto: orario.eccezione? orario.eccezione.split(' ') : [],
            orari:[ orario.dalle + "-" + orario.alle ]
          });
        } else {
          if ($scope.orari[j].orari.indexOf(orario.dalle + "-" + orario.alle) == -1) {
            $scope.orari[j].orari.push(orario.dalle + "-" + orario.alle);
          }
        }
      });
    });
    Raccolta.raccolta({ tipopunto:$scope.pdr.tipologiaPuntiRaccolta }).then(function(raccolta){
      var myRifiuti=[];
      var myRifiutiId=[];
      raccolta.forEach(function(regola){
        if (!Utili.pdrCharacteristicApplies($scope.pdr, regola.tipologiaRaccolta)) return;
        var tipoRaccolta = DataManager.getCategoriaById('tipologiaRaccolta', regola.tipologiaRaccolta);
        if (myRifiutiId.indexOf(tipoRaccolta.id)==-1) {
          myRifiuti.push(tipoRaccolta);
          myRifiutiId.push(tipoRaccolta.id);
        //} else { console.log('already: '+regola.tipologiaRaccolta);
        }
      });
      $scope.rifiuti=myRifiuti;
    });
  });
})

.controller('InfoRaccoltaCtrl', function ($scope, $rootScope, $stateParams, $ionicNavBarDelegate, Raccolta) {

  $scope.id = $stateParams.id;
  $scope.pdr = {};
  $scope.orari = [];
  $scope.note = [];
  //[{giorno:"lunedì",orari:["12.00-14.00","15.30-17.30"...]}...]

  $scope.checkGiorni = function (item) {
    for (var j = 0; j < $scope.orari.length; j++) {
      if ($scope.orari[j].giorno == item) return j;
    }
    return -1;
  };

  Raccolta.puntiraccolta({ tipo:$scope.id, all:true }).then(function(punti){
    $scope.pdr = punti[0];
    punti.forEach(function(punto){
      punto.orarioApertura.forEach(function(orario) {
      var j = $scope.checkGiorni(orario.il);
        if (j == -1) {
          $scope.orari.push({
            giorno: orario.il.split(' '),
            ecceto: orario.eccezione? orario.eccezione.split(' ') : [],
            orari:[ orario.dalle + "-" + orario.alle ],
            note: [orario.note]
          });
        } else {
          if ($scope.orari[j].orari.indexOf(orario.dalle + "-" + orario.alle) == -1) {
            $scope.orari[j].orari.push(orario.dalle + "-" + orario.alle);
            $scope.orari[j].note.push(orario.note);
          }
        }
        if ($scope.note.indexOf(orario.note) == -1) {
            $scope.note.push(orario.note);
        }
      });
    });
    Raccolta.raccolta({ tipopunto:$scope.pdr.tipologiaPuntiRaccolta }).then(function(raccolta){
      var myRifiuti=[];
      var myRifiutiUniqueCounter=[];
      raccolta.forEach(function(regola){
        if (myRifiutiUniqueCounter.indexOf(regola.tipoRaccolta.id)==-1) {
          var tipo = {};
          tipo['id'] = regola.tipoRaccolta.id;
          tipo['nome'] = regola.tipoRaccolta.nome;
          myRifiuti.push(tipo);
          myRifiutiUniqueCounter.push(regola.tipoRaccolta.id);
        //} else { console.log('already: '+regola.tipologiaRaccolta);
        }
      });
      $scope.rifiuti=myRifiuti;
    });
  });
})
