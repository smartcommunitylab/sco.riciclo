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
                  var tipologiaId = $scope.rifiuti[i].tipologiaRifiuto.trim();
                  var tipologiaLabel = $scope.tipiDiRifiuti[tipologiaId].nome;
                  var tipologiaIcona = $scope.tipiDiRifiuti[tipologiaId].icona;
				  if(!tipologiaIcona) {
					  tipologiaIcona = tipologiaLabel;
				  }
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
  
.controller('PDRCtrl', function ($scope, $rootScope, $timeout, Raccolta, $location, $stateParams, $http, Utili, DataManager, LoaderService) {


  var mapCenter = {
      latitude: CENTER.latitude,
      longitude: CENTER.longitude
    };

  var mapZoom = ZOOM;

  if(ionic.Platform.isWebView()){
     $http.get('https://maps.google.it').success(function (data) {
                //LoaderService.showToast('accesso!');
            }).error(function (data, status) {
                LoaderService.showToast("map_connection_toast");
            });
  }

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
    center: mapCenter,
    zoom: mapZoom,
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
  };

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

      $scope.hasSomePoints = true;

      if(points.length==0){
        LoaderService.showToastByTime("zero_points_msg",4000);
        $scope.hasSomePoints = false;
      }

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

.controller('RaccoltaCtrl', function ($scope, $stateParams, $rootScope, Raccolta, Utili) {
  $scope.id = $rootScope.id2addr($stateParams.id);

  //$scope.id = $stateParams.id;

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

.controller('RifiutiCtrl', function ($scope, $stateParams, $ionicConfig, $ionicHistory, $rootScope, Raccolta) {
  $scope.tipo = $rootScope.id2addr($stateParams.tipo);
  //$scope.tipo = $stateParams.tipo;

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

.controller('RifiutoCtrl', function ($scope, $ionicHistory, $rootScope, $stateParams, Raccolta) {
  $scope.nome = $rootScope.id2addr($stateParams.nome);

  var hasCustomBackFn = function() {
    var hist = $ionicHistory.viewHistory();
    return hist.backView && hist.backView.stateId == 'app.home.tipidirifiuti';
  };

  $scope.hasCustomBack = hasCustomBackFn();

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

  $scope.$on('$ionicView.beforeEnter', function (event, viewData) {
    viewData.enableBack = true;
  });

  $scope.id = !!$stateParams.id && $stateParams.id != 'undefined' && $stateParams.id != 'null'? $rootScope.id2addr($stateParams.id) : null;
  $scope.pdr = {};
  $scope.orari = [];
  //[{giorno:"lunedì",orari:["12.00-14.00","15.30-17.30"...]}...]

  var today = new Date();

  $scope.checkGiorni = function (item) {
    for (var j = 0; j < $scope.orari.length; j++) {
      if ($scope.orari[j].giorno == item) return j;
    }
    return -1;
  };

  var dayInWeek = ["lunedì","martedì","mercoledì","giovedì","venerdì","sabato","domenica"];

  var orderOrari = function(){
    var fixedDays = [];
    var ashDayOrari = {};
    var orderedOrari = [];

    $scope.orari.forEach(function(orario){
        if(dayInWeek.indexOf(orario.giorno[0])>-1){
            if(ashDayOrari[orario.giorno]==null){
                ashDayOrari[orario.giorno] = [];
            }

            ashDayOrari[orario.giorno].push(orario);
        }else{
            fixedDays.push(orario);
        }
    })

    dayInWeek.forEach(function(day){
        if(!!ashDayOrari[day]){
            ashDayOrari[day].forEach(function(dayAr){
                var orario = dayAr;
                orderedOrari.push(orario);
            });
        }
    })

    fixedDays.forEach(function(orario){
        orderedOrari.push(orario);
    })

    $scope.orari = orderedOrari;
  }

  var getDateBySlpittedDate = function (splittedeDate){
    var year = null;
    var month = null;
    var day = null;
    var date = null;

    try{
        year = parseInt(splittedeDate[0]);
        month = parseInt(splittedeDate[1]);
        day = parseInt(splittedeDate[2]);

        date = new Date(year, month-1, day);
    }catch(err){
        console.log("invalid date: "+splittedeDate);
        return null;
    }

    return date;
  }

  var isInDateInRange = function (orario){
    if(!!orario){
        var orarioDataDa = orario.dataDa.split("-");
        var orarioDataA = orario.dataA.split("-");
        if((!!orarioDataDa && orarioDataDa.length==3)&&
           (!!orarioDataA && orarioDataA.length==3)){
            var dataDa = getDateBySlpittedDate(orarioDataDa);
            var dataA = getDateBySlpittedDate(orarioDataA);

            if(dataDa<=today && dataA>=today){
                return true;
            }
        }
    }

    return false;
  }

  $scope.clickNav = function() {
    if ($scope.pdr.localizzazione) window.open("http://maps.google.com?daddr="+$scope.pdr.localizzazione,"_system");
    else window.open("http://maps.google.com?daddr="+$scope.pdr.dettagliZona,"_system");
  };
  
  Raccolta.puntiraccolta({ zona:$scope.id, all:true }).then(function(punti){
    $scope.pdr = punti[0];
    punti.forEach(function(punto){
      punto.orarioApertura.forEach(function(orario) {

        if(isInDateInRange(orario)){
            var j = $scope.checkGiorni(orario.il);

            var hour = orario.dalle;
            if(orario.dalle == null || orario.alle == null){
              if(orario.dalle != null){
                   hour = orario.dalle;
              }
              if(orario.alle != null){
                  hour = orario.alle;
              }
              if(orario.alle == null && orario.dalle == null){
                  hour = '';
              }
            }else{
              if(orario.dalle != orario.alle){
                hour = orario.dalle +'-'+orario.alle;
              }
            }

            if (j == -1) {
              $scope.orari.push({
                giorno: orario.il.split(' '),
                ecceto: orario.eccezione? orario.eccezione.split(' ') : [],
                orari:[ hour ]
              });
            } else {
              if ($scope.orari[j].orari.indexOf(hour) == -1) {
                $scope.orari[j].orari.push(hour);
              }
            }
        }
      });
    });

    orderOrari();

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

  $scope.$on('$ionicView.beforeEnter', function (event, viewData) {
    viewData.enableBack = true;
  });

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

        var hour = orario.dalle;
        if(orario.dalle == null || orario.alle == null){
          if(orario.dalle != null){
               hour = orario.dalle;
          }
          if(orario.alle != null){
              hour = orario.alle;
          }
          if(orario.alle == null && orario.dalle == null){
              hour = '';
          }
        }else{
          if(orario.dalle != orario.alle){
            hour = orario.dalle +'-'+orario.alle;
          }
        }

        if (j == -1) {
          $scope.orari.push({
            giorno: orario.il.split(' '),
            ecceto: orario.eccezione? orario.eccezione.split(' ') : [],
            orari:[ hour ],
            note: [orario.note]
          });
        } else {
          if ($scope.orari[j].orari.indexOf(hour) == -1) {
            $scope.orari[j].orari.push(hour);
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
