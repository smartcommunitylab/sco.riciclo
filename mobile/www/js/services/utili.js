angular.module('rifiuti.services.utili', [])

.factory('Conf', function() {

    return {
        showNews: function() {
            return SHOW_NEWS;
        }
    };
})

.factory('ConnectionErrorService', function($ionicPopup,$filter) {
  return {
    show : function() {
        var popup = $ionicPopup.show({
            title: '<b class="popup-title">'+$filter("translate")("dev_mode")+'<b/>',
            template: $filter("translate")("connection_error"),
            buttons: [
                {
                    text: 'OK'
                }
            ]
        });
     }
   }
})

.factory('LoaderService', function($rootScope, $ionicLoading, $ionicPopup, $filter) {
  return {

        showToastByTime : function(alertMsg, time){
            $ionicLoading.show({
                template:  $filter("translate")(alertMsg),
                noBackdrop: true,
                duration: time });
        },

        showToast : function(alertMsg){
            $ionicLoading.show({
                template:  $filter("translate")(alertMsg),
                noBackdrop: true,
                duration: 3000 });
        },

        show : function() {

            $rootScope.loading = $ionicLoading.show({

              // The text to display in the loading indicator
              content: '<i class="icon ion-looping"></i> Loading',

              // The animation to use
              animation: 'fade-in',

              // Will a dark overlay or backdrop cover the entire view
              showBackdrop: true,

              // The maximum width of the loading indicator
              // Text will be wrapped if longer than maxWidth
              maxWidth: 200,

              // The delay in showing the indicator
              showDelay: 10
            });
        },

        hide : function(){
            if ($rootScope.loading) {
                $ionicLoading.hide();
            }
        },

        showPopUp : function(msg, title) {
            var popUpTitle = 'warning';

            if(!!title){
                popUpTitle = title;
            }

            $ionicPopup.show({
                title: '<b class="popup-title">'+$filter("translate")(popUpTitle)+'<b/>',
                template: $filter("translate")(msg),
                buttons: [
                    {
                        text: 'OK'
                    }
                ]
            });
        }

    }
})
.factory('Utili', function () {
  var mesi = ["Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"];
  var giorni = ["DOM", "LUN", "MAR", "MER", "GIO", "VEN", "SAB"];
  var giorniC = ["domenica", "lunedì", "martedì", "mercoledì", "giovedì", "venerdì", "sabato"];
  var giorniShort = ["dom", "lun", "mar", "mer", "gio", "ven", "sab"];


  var DOW = {"DOM":6, "LUN":0, "MAR":1, "MER":2, "GIO":3, "VEN":4, "SAB":5};

  var daysInMonth = function(month, year) {
      return new Date(year, month + 1, 0).getDate();
  };
  var dayIndex = function(day) {
          return DOW[day];
  };

  var getListFromString = function(separator, stringList){
    var list = stringList.split(separator);

    return list;
  }

  var format = function(date) {
    return date.getFullYear()+'-'+(date.getMonth() < 9 ? '0'+(date.getMonth()+1):(date.getMonth()+1))+'-'+(date.getDate() < 10 ? '0'+date.getDate():date.getDate());
  };

  var generateDOWDates = function(da, a, dow, ecc) {
      var res = [];
      var currFrom = new Date(da.getTime());
      currFrom.setDate(currFrom.getDate()-dayIndex(giorni[currFrom.getDay()])+dow);
      while (currFrom.getTime() <= a.getTime()) {
        if (currFrom.getTime() >= da.getTime()) {
          var dStr = format(currFrom);
          if (!ecc || ecc.indexOf(dStr) < 0) res.push(dStr);
        }
        // move to next week
        currFrom.setDate(currFrom.getDate()+7);
      }
      return res;
  };
  
  /**
   * generate a list of date string for the specified period, date, definition, and list of exceptions (format dd/MM[/yyyy])
   * definition is space-separated elements, where element is either DOW (e.g., lunedi) or date (format dd/MM[/yyyy])
   */
  var calToDates = function(da, a, il, ecc) {
    if (!il) return [];

    var arr = il.trim().split(' ');
    var dates = [];  
    var eccDates = calToDates(da, a, ecc, null);
    for (var i = 0; i < arr.length; i++) {
      var elem = arr[i];
      if (elem == "") continue;
      var idx = giorniC.indexOf(elem);          
      // DOW case
      if (idx >= 0) {
        dates = dates.concat(generateDOWDates(da, a, DOW[giorni[idx]], eccDates));
      // date case
      } else {
        var dElems = elem.split('/');
        if (dElems.length < 2 || dElems.length > 3) return;
        if (dElems.length == 2) dElems.push(''+da.getFullYear());
        var dStr = format(new Date(dElems[2], dElems[1]-1, dElems[0]));
        if (!ecc || ecc.indexOf(dStr) < 0) dates.push(dStr);
      }
    }
    dates.sort();
    return dates;
  };


  var iconType = function(tipologia) {
    if (tipologia in ICON_POINT_MAP) return ICON_POINT_MAP[tipologia];
    return null;
  }; 
  
  var rgbColor = function(colore) {
      if (colore in ICON_COLOR_MAP) return ICON_COLOR_MAP[colore];
      return 'grey';
  }

  return {
    getListFromString: getListFromString,
    jsDOWToShortText: function(dow) {
      return giorni[dow];
    },
    jsDOWToDOW: function(dow) {
      return DOW[giorni[dow]];
    },
    DOWTextToDOW: function(dow) {
      return DOW[dow];
    },
    dayIndex: dayIndex,
    textToDOW: function(txt) {
      return DOW[giorni[giorniC.indexOf(txt)]];
    },
    dayToDOW: function(day) {
      return dayIndex(giorni[day]);
    },
    monthYear: function(a, b) {
        return mesi[a] + " " + b
    },
    isLastDayInMonth : function(dt) { 
        return new Date(dt.getTime() + 86400000).getDate() === 1;
    },
    lastDateOfMonth: function(date) {
        return new Date(date.getFullYear(), date.getMonth() + 1, 0);
    },
    calToDates: calToDates,
    expandOrarioApertura: function(cal) {
      if (!cal.il || cal.dates) return;
      var max = new Date(Date.parse(cal.dataA));
      var min = new Date(Date.parse(cal.dataDa));
      cal.dates = calToDates(min,max, cal.il, cal.eccezione);
    }, 

    getRGBColor: function(rgbColor) {
        var c = rgbColor;
        if (c == 'white') {
            return 'grey';
        }
        return c;
    },
    iconFromRegola: function(regola) {
      return this.icon(regola.tipologiaPuntoRaccolta, regola.colore);
    },
//    icon: function(tipologia, colore) {
//      var icona = iconType(tipologia);
//      return (!!icona?'img/ic_'+icona+'_'+this.getRGBColor(colore)+'.png':null);
//    },
    icon: function(icona,colorById) {
      //var icona = iconType(tipologia);
      if (colorById=='white') {
        return icona + '-outline';
      }
      return icona;
    },
    poiIcon: function(icona, colore) {
      //var icona = iconType(tipologia);
      return (!!icona?'img/ic_poi_'+icona+'.png':null);
    },
    belongsTo: function(pr, area, profile) {
      return profile.aree.indexOf(area) != -1
          //&& (pr.tipologiaPuntiRaccolta=='CRM' || pr.tipologiaPuntiRaccolta=='CRZ' || !pr.zona || profile.comuni.indexOf(pr.zona)!=-1)
        ;
    },
    isPaP: function(tipoPuntoRaccolta) {
      return !!tipoPuntoRaccolta && !!tipoPuntoRaccolta.type && (tipoPuntoRaccolta.type == 'PP' || !!CUSTOM_PAP && CUSTOM_PAP.indexOf(tipoPuntoRaccolta.type) >= 0);
    },
      //return !!tipologia && (tipologia.toLowerCase().indexOf('porta a porta') == 0 || !!CUSTOM_PAP && CUSTOM_PAP.indexOf(tipologia) >= 0);
	fullDateFormat: function(d,transf) {
		return transf(giorniShort[d.getDay()])+ ' ' +d.getDate()+' '+transf(mesi[d.getMonth()])+' '+d.getFullYear();
	},
//    filterCharacteristics: function(pdr) {
//        if (!ISOLA_MAPPING) return false;
//        if (!pdr.caratteristiche) return false;
//        for (var key in pdr.caratteristiche) {
//            if (pdr.caratteristiche[key]) return true;
//        }
//        return false;
//    },
    pdrCharacteristicApplies: function(pdr, tipo) {
        var mapping = null;
        try {
            mapping = ISOLA_MAPPING;
        }catch(e) {
        }
        if (mapping != null && pdr.caratteristiche && !pdr.caratteristiche[mapping[tipo]]) return false;
        return true;
    }
  }
})
