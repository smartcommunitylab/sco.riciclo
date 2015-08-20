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

  var init = function() {
    if ($scope.selectedProfile) {
      Raccolta.rifiuti().then(function(data){
        $scope.rifiuti = data;
        Raccolta.immagini().then(function(){
          var tipologieMap = {};
          var results=[], row=[], counter=-1;
          for (var i = 0; i < $scope.rifiuti.length; i++) {
              var tipologia = $scope.rifiuti[i].tipologiaRifiuto;
              if (!(tipologia in tipologieMap)) tipologieMap[tipologia] = {label:tipologia, img:$scope.immagini[tipologia]};
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
    }
  };
  
  $rootScope.$watch('selectedProfile',function(a,b) {
    if (b == null || a.id != b.id) {
      init();
    }
  }); 

  init();
  
})
  
.controller('PDRCtrl', function ($scope, $rootScope, $timeout, Raccolta, $location, $stateParams, Utili) {

  $scope.profile = null;
  
  $rootScope.checkMap();  
  
  $scope.updateIMG = function () {
    $scope.variableIMG = $scope.mapView ? "riciclo-lista" : "riciclo-map";
  };

  $scope.openMarkerClick = function ($markerModel) {
    $location.url('/app/puntoDiRaccolta/' + $rootScope.addr2id($markerModel.model.id));
  };

  $scope.map = {
    control: {},
    center: {
      latitude: 46.0,
      longitude: 11.0
    },
    zoom: 8,
    pan: false,
    draggable: 'true',
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

  $scope.click = function () {
    $scope.mapView = !$scope.mapView;
    $scope.updateIMG();
//    $timeout(function () {
//      var mapHeight = 800; // or any other calculated value
//      var mapContainer = document.querySelector('#map-container');
//      if (mapContainer) {
//        mapHeight = angular.element(mapContainer)[0].offsetHeight;
//      } else { 
//        console.log('cannot get "#map-container"');
//      }
//      var ng_mapContainer = document.querySelector('.angular-google-map-container');
//      if (ng_mapContainer) {
//        angular.element(ng_mapContainer)[0].style.height = mapHeight + 'px';
//      } else { 
//        console.log('cannot get ".angular-google-map-container"');
//      }
//    }, 200);
  };

//  $scope.$on('$viewContentLoaded', function () {
//    $timeout(function () {
//      var mapHeight = 800; // or any other calculated value
//      mapHeight = angular.element(document.querySelector('#map-container'))[0].offsetHeight;
//      angular.element(document.querySelector('.angular-google-map-container'))[0].style.height = mapHeight + 'px';
//    }, 50);
//  });

  var addToList = function (item, list) {
    for (var i = 0; i < list.length; i++) {
      if (list[i].tipologiaPuntoRaccolta == item.tipologiaPuntiRaccolta) {
        list[i].locs.push(item);
        return;
      }
    }
    list.push({
      aperto: false,
      tipologiaPuntoRaccolta: item.tipologiaPuntiRaccolta,
      icon: Utili.icon(item.tipologiaPuntiRaccolta),
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
        if ($scope.id == null && !!punto.dettagliZona || punto.dettagliZona == $scope.id) {
          var icon = {
            url: Utili.poiIcon(punto.tipologiaPuntiRaccolta),
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
        fit: true,
        icon: 'icon',
        doCluster: points.length > 10
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
    return Utili.icon(item.tipologiaPuntoRaccolta,item.colore);
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
    var tipirifiuto=[], tipipunto=[];
    raccolta.forEach(function(regola){
      if (tipirifiuto.indexOf(regola.tipologiaRifiuto)==-1) tipirifiuto.push(regola.tipologiaRifiuto);
      if (tipipunto.indexOf(regola.tipologiaPuntoRaccolta)==-1) tipipunto.push(regola.tipologiaPuntoRaccolta);
    });

    Raccolta.rifiuti({ tipi:tipirifiuto }).then(function(rifiuti){
      $scope.rifiuti=rifiuti;
    });

    Raccolta.raccolta({ tipipunto:tipipunto, tipirifiuto:tipirifiuto }).then(function(raccolta){
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

.controller('RifiutiCtrl', function ($scope, $stateParams, $ionicConfig, $ionicHistory, Raccolta) {
  $scope.tipo = $stateParams.tipo;

  $scope.backButtonStyle = $ionicConfig.backButton.icon();

  Raccolta.raccolta({ tiporifiuto:$scope.tipo }).then(function(raccolta){
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

.controller('PuntoDiRaccoltaCtrl', function ($scope, $rootScope, $stateParams, $ionicNavBarDelegate, Raccolta) {

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
      raccolta.forEach(function(regola){
        if (myRifiuti.indexOf(regola.tipologiaRaccolta)==-1) {
          myRifiuti.push(regola.tipologiaRaccolta);
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
      raccolta.forEach(function(regola){
        if (myRifiuti.indexOf(regola.tipologiaRaccolta)==-1) {
          myRifiuti.push(regola.tipologiaRaccolta);
        //} else { console.log('already: '+regola.tipologiaRaccolta);
        }
      });
      $scope.rifiuti=myRifiuti;
    });
  });
})
