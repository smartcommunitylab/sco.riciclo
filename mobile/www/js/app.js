// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.controllers' is found in controllers.js
angular.module('rifiuti', [
    'ionic',
    'ngCordova',
    'rifiuti.filters',
    'rifiuti.directives',
    'rifiuti.services.data',
    'rifiuti.services.profili',
    'rifiuti.services.rifiuti',
    'rifiuti.services.utili',
    'rifiuti.services.geo',
    'rifiuti.services.feed',
    'rifiuti.services.calendar',
    'rifiuti.controllers.common',
    'rifiuti.controllers.home',
    'rifiuti.controllers.raccolta',
    'rifiuti.controllers.profilo',
    'pascalprecht.translate',
    'uiGmapgoogle-maps',
    //'google-maps',
    'angular.filter',
    'ionic-timepicker'
])

.config(function (uiGmapGoogleMapApiProvider) {
    uiGmapGoogleMapApiProvider.configure({
        key: API_KEY,
        v: '3.17',
        libraries: 'geometry'
    });
})

//.config(['GoogleMapApiProvider'.ns(), function (GoogleMapApi) {
//  GoogleMapApi.configure({
//    //    key: 'your api key',
//    v: '3.17',
////    libraries: 'geometry'
//  });
//}])

/*
.config(function ($ionicTabsConfig) {
    // Override the Android platform default to add "tabs-striped" class to "ion-tabs" elements.
    $ionicTabsConfig.type = '';
})
*/

/* NEW in beta14*/
.config(function ($ionicConfigProvider) {
    $ionicConfigProvider.tabs.position('top');
    //$ionicConfigProvider.tabs.style('striped');
    $ionicConfigProvider.tabs.style('standard');
    $ionicConfigProvider.backButton.text('');
})

.run(function ($ionicPlatform, $rootScope, $ionicNavBarDelegate, $ionicHistory, $translate, $ionicPopup, $filter, $state, Profili, GeoLocate, $cordovaSplashscreen, $ionicLoading, $ionicConfig) {
    $rootScope.version = '2.0';

    $rootScope.isWebView = ionic.Platform.isWebView();
    $rootScope.isPopUp = !$rootScope.isWebView && !IF_HIDDEN_FIELDS;
    $rootScope.isHiddenFields = IF_HIDDEN_FIELDS;
    $rootScope.loadingShow = function () {
        $ionicLoading.show({
            template: '<ion-spinner></ion-spinner>'
        });
    };

    $rootScope.showAlert = function(link) {
        var alertPopup = $ionicPopup.alert({
            title: 'Attenzione!',
            template: 'Non disponibile nella versione Web',
            buttons: [
            {
                text: 'OK',
                type: 'button-100r'
            }
          ]
        });
   };


    $rootScope.addr2id = function(addr) {
        return addr ? addr.replace('/','_') : null;
    }
    $rootScope.id2addr = function(id) {
        return id ? id.replace('_','/') : null;
    }

    $rootScope.loadingHide = function () {
        $ionicLoading.hide();
    };

    $rootScope.loadingShow();


    $rootScope.profili = [];
    $rootScope.selectedProfile = null;

    $rootScope.selectProfile = function (index) {
        Profili.select(index);
    };


    $rootScope.back = function () {
        if ($rootScope.profili == null || $rootScope.profili.length == 0) {
            ionic.Platform.exitApp();
            return;
        }
        //$ionicNavBarDelegate.$getByHandle('navBar').back();
        $ionicHistory.goBack();
    };

    // for BlackBerry 10, WP8, iOS
    setTimeout(function () {
        $cordovaSplashscreen.hide();
        //navigator.splashscreen.hide();
    }, 3000);

    $rootScope.locationWatchID = undefined;
    //  ionic.Platform.fullScreen(false,true);
    if (typeof (Number.prototype.toRad) === "undefined") {
        Number.prototype.toRad = function () {
            return this * Math.PI / 180;
        }
    }

    document.addEventListener("pause", function () {
        console.log('app paused');
        if (typeof $rootScope.locationWatchID != 'undefined') {
            navigator.geolocation.clearWatch($rootScope.locationWatchID);
            $rootScope.locationWatchID = undefined;
            GeoLocate.reset();
            console.log('geolocation reset');
        }
    }, false);

    document.addEventListener("resume", function () {
        console.log('app resumed');
        GeoLocate.locate();
    }, false);

    GeoLocate.locate().then(function (position) {
        $rootScope.myPosition = position;
        //console.log('first geolocation: ' + position);
    }, function () {
        console.log('CANNOT LOCATE!');
    });

    var backCallback = function (event) {
        console.log('going back in ' + $state.current.name);
        if ($rootScope.profili == null || $rootScope.profili.length == 0) {
            ionic.Platform.exitApp();
            return;
        }
        switch ($state.current.name) {
        case "app.home.tipidirifiuti":
            //              case "app.home":
            //              case "app.home.note":
            //              case "app.home.calendario":
            //              case "app.puntiDiRaccolta":
            //              case "app.tipiDiRaccolta":
            //              case "app.rifiuti":
            //              case "app.profili":
            //              case "app.segnala":
            //              case "app.contatti":
            //              case "app.info":
            $rootScope.reallyexitapp();
            break;
        default:
            navigator.app.backHistory();
        }
    };
    //$ionicPlatform.onHardwareBackButton(backCallback);
    $ionicPlatform.registerBackButtonAction(backCallback, 100);

    $ionicPlatform.ready(function () {
        $rootScope.platform = ionic.Platform;
        $rootScope.backButtonStyle = $ionicConfig.backButton.icon();

        // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
        // for form inputs)
        if (window.cordova && window.cordova.plugins.Keyboard) {
            cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
        }
        if (window.StatusBar) {
            // org.apache.cordova.statusbar required
            StatusBar.styleDefault();
        }

        if (typeof navigator.globalization !== "undefined") {
            navigator.globalization.getPreferredLanguage(function (language) {
                $translate.use((language.value).split("-")[0]).then(function (data) {
                    console.log("SUCCESS -> " + data);
                }, function (error) {
                    console.log("ERROR -> " + error);
                });
            }, null);
        }

        if (localStorage.profiles && localStorage.profiles.length > 0) {
            Profili.updateNotifications();
        }

    });

    window.addEventListener('filePluginIsReady', function () {
        console.log('device is ready');
    }, false);

    $rootScope.checkMap = function () {
        if (window.google != null && window.google.maps != null) {
            angular.module('rifiuti').requires.push('google-maps');
        }
    };

    $rootScope.clickLink = function (link) {
        window.open(link, "_system");
    };

    //    $rootScope.distance = function (pt1, pt2) {
    //        var d = false;
    //        if (pt1 && pt1[0] && pt1[1] && pt2 && pt2[0] && pt2[1]) {
    //            var lat1 = parseFloat(pt1[0]);
    //            var lon1 = parseFloat(pt1[1]);
    //            var lat2 = parseFloat(pt2[0]);
    //            var lon2 = parseFloat(pt2[1]);
    //
    //            var R = 6371; // km
    //            //var R = 3958.76; // miles
    //            var deg2rad = Math.PI / 180;
    //            var dLat = (lat2 - lat1) * deg2rad; // deg2rad below
    //            var dLon = (lon2 - lon1) * deg2rad;
    //            var a = 0.5 - Math.cos(dLat) / 2 + Math.cos(lat1 * deg2rad) * Math.cos(lat2 * deg2rad) * (1 - Math.cos(dLon)) / 2;
    //            d = R * 2 * Math.asin(Math.sqrt(a));
    //        } else {
    //            console.log('cannot calculate distance!');
    //        }
    //        return d;
    //    };

    $rootScope.reallyexitapp = function () {
        $ionicPopup.show({
            title: $filter('translate')('Progetto'),
            template: $filter('translate')('exitapp_template'),
            buttons: [
                {
                    text: $filter('translate')('cancel')
                },
                {
                    text: '<b>' + $filter('translate')('exitapp_ok') + '</b>',
                    type: 'button-100r',
                    onTap: function (e) {
                        return true;
                    }
        }
      ]
        }).then(function (reallyExit) {
            if (reallyExit) ionic.Platform.exitApp();
        });
    };
})


.config(function ($stateProvider, $urlRouterProvider, $translateProvider) {

    var lang = navigator.language.split("-");
    var current_lang = (lang[0]);
    //alert( "current_lang: " + current_lang );

    $translateProvider.translations("it", {
        hello_message: "ciao",
        goodbye_message: "arrivederci",
        exitapp_template: "Sei sicuro di uscire dall'app?",
        what_remember: "Cosa vuoi ricordare?",
        save: "Salva",
        cancel: "Annulla",
        set: 'Imposta',
        exitapp_ok: "OK",
        LUN: "LUN",
        MAR: "MAR",
        MER: "MER",
        GIO: "GIO",
        VEN: "VEN",
        SAB: "SAB",
        DOM: "DOM",
        lun: "lunedì",
        mar: "martedì",
        mer: "mercoledì",
        gio: "giovedì",
        ven: "venerdì",
        sab: "sabato",
        dom: "domenica",
        Gennaio: "gennaio",
        Febbraio: "febbraio",
        Marzo: "marzo",
        Aprile: "aprile",
        Maggio: "maggio",
        Giugno: "giugno",
        Luglio: "luglio",
        Agosto: "agosto",
        Settembre: "settembre",
        Ottobre: "ottobre",
        Novembre: "novembre",
        Dicembre: "dicembre",
        Tocca: "Tocca + per aggiungere una nota",
        empty_elem: "Nessun elemento da visualizzare",
        'ad esempio': "Ad esempio:",
        Invia: "Invia una email per segnalare un problema direttamente all'ente che si occupa della gestione dei rifiuti. Puoi allegare una foto e le coordinate GPS della tua posizione.",
        Progetto: '"' + APP_NAME + '"',
        Progetto_DESC: 'Tutto quesllo che devi sapere sulla raccolta differenziata nel tuo Comune',
        Progetto_di: 'Un progetto di:',
        Collaborazione: "In collaborazione con:",
        Eventuali: "Per informazioni:",
        TutorialUno: "Questo tutorial ti illustrerà il funzionamento della app. Per sapere dove buttare uno specifico rifiuto, scrivine il nome qui e premi sulla lente d'ingrandimento.",
        TutorialDue: "Scopri quali rifiuti appartengono ad una determinata categoria e scopri dove devono essere conferiti",
        TutorialTre: "Aggiungi delle note personali o dei promemoria legati alla gestione dei rifiuti (e.g. pagamento della bolletta, oggetti da portare al CRM, etc)",
        TutorialNews: "Tieniti informato delle novità della raccolta nel tuo comune",
        TutorialQuattro: "Tieni sotto controllo le scadenze della raccolta porta a porta e aggiungi delle note personali.",
        TutorialCinque: "Premi qui per aprire il menù laterale e scoprire ulteriori funzionalità",
        TTUno: "Benvenuto!",
        TTDue: "Tipologie di rifiuto",
        TTTre: "Note",
        TTNews: "News",
        TTQuattro: "Calendario",
        TTCinque: "Menù laterale",
        Selezionare: "Seleziona Comune/Località",
        "Le mie note": "Le mie note",
        "Impostazioni": "Impostazioni",
        "NOTE": "NOTE",
        "Note": "Note",
        "NEWS": "NEWS",
        "News": "News",
        pick_time: 'Scegli l\'orario'
    });
    $translateProvider.preferredLanguage(current_lang);

    //debug only
    //$translateProvider.preferredLanguage("en"); // solo lingua inglese, commentare per attivare il riconoscimento della lingua automatico
    //end debug only

    $translateProvider.fallbackLanguage("it");

    $stateProvider
        .state('app', {
            url: "/app",
            abstract: true,
            templateUrl: "templates/menu.html",
            controller: 'AppCtrl'
        })

    .state('app.home', {
        url: "/home",
  //      abstract: true,
        views: {
            'menuContent': {
                templateUrl: "templates/home.html",
                controller: 'HomeCtrl'
            }
        }
    })

    .state('app.home.note', {
        url: "/note",
        views: {
            'note': {
                templateUrl: "templates/home/note.html",
                controller: 'noteCtrl'
            }
        }
    })

    .state('app.home.news', {
        url: "/news",
        views: {
            'news': {
                templateUrl: "templates/home/news.html",
                controller: 'newsCtrl'
            }
        }
    })

    .state('app.home.newsitem', {
        url: "/newsitem/:id",
        views: {
            'news': {
                templateUrl: "templates/home/newsitem.html",
                controller: 'newsItemCtrl'
            }
        }
    })

    .state('app.home.tipidirifiuti', {
        url: "/tipidirifiuti",
        views: {
            'tipidirifiuti': {
                templateUrl: "templates/home/tipidirifiuti.html",
                controller: 'tipidirifiutiCtrl'
            }
        }
    })

    .state('app.home.calendario', {
        url: "/calendario",
        views: {
            'calendario': {
                templateUrl: "templates/home/calendario.html",
                controller: 'calendarioCtrl'
            }
        }
    })

    .state('app.puntiDiRaccolta', {
        url: "/puntiDiRaccolta/:id",
        cache: false,
        views: {
            'menuContent': {
                templateUrl: "templates/puntiDiRaccolta.html",
                controller: 'PDRCtrl'
            }
        }
    })

    .state('app.tipiDiRaccolta', {
        url: "/tipiDiRaccolta",
        views: {
            'menuContent': {
                templateUrl: "templates/tipiDiRaccolta.html",
                controller: 'TDRCtrl'
            }
        }
    })

    .state('app.raccolta', {
        url: "/raccolta/:id",
        views: {
            'menuContent': {
                templateUrl: "templates/raccolta.html",
                controller: 'RaccoltaCtrl'
            }
        }
    })

    .state('app.rifiuti', {
        url: "/rifiuti/:tipo",
        views: {
            'menuContent': {
                templateUrl: "templates/rifiuti.html",
                controller: 'RifiutiCtrl'
            }
        }
    })

    .state('app.rifiuto', {
        url: "/rifiuto/:nome",
        views: {
            'menuContent': {
                templateUrl: "templates/rifiuto.html",
                controller: 'RifiutoCtrl'
            }
        }
    })

    .state('app.puntoDiRaccolta', {
        url: "/puntoDiRaccolta/:id",
        cache: false,
        views: {
            'menuContent': {
                templateUrl: "templates/puntoDiRaccolta.html",
                controller: 'PuntoDiRaccoltaCtrl'
            }
        }
    })

    .state('app.profili', {
        url: "/profili",
        views: {
            'menuContent': {
                templateUrl: "templates/profili.html",
                controller: 'ProfiliCtrl'
            }
        }
    })

    .state('app.aggProfilo', {
        url: "/aggProfilo",
        views: {
            'menuContent': {
                templateUrl: "templates/modificaProfilo.html",
                controller: 'ModificaProfiloCtrl'
            }
        }
    })

    .state('app.modificaProfilo', {
        url: "/modificaProfilo/:id",
        views: {
            'menuContent': {
                templateUrl: "templates/modificaProfilo.html",
                controller: 'ModificaProfiloCtrl'
            }
        }
    })

    .state('app.segnala', {
        url: "/segnala",
        views: {
            'menuContent': {
                templateUrl: "templates/segnala.html",
                controller: 'SegnalaCtrl'
            }
        }
    })

    .state('app.settings', {
        url: "/settings",
        cache: false,
        views: {
            'menuContent': {
                templateUrl: "templates/settings.html",
                controller: 'SettingsCtrl'
            }
        }
    })

    .state('app.menunote', {
        url: "/menunote",
        views: {
            'menuContent': {
                templateUrl: "templates/menunote.html",
                controller: 'noteCtrl'
            }
        }
    })

    .state('app.contatti', {
        url: "/contatti",
        views: {
            'menuContent': {
                templateUrl: "templates/contatti.html",
                controller: 'ContattiCtrl'
            }
        }
    })

    .state('app.contatto', {
        url: "/contatti/:id",
        views: {
            'menuContent': {
                templateUrl: "templates/contatto.html",
                controller: 'ContattoCtrl'
            }
        }
    })

    .state('app.info', {
        url: "/info",
        views: {
            'menuContent': {
                templateUrl: "templates/info.html",
                controller: 'InfoCtrl'
            }
        }
    });

    $urlRouterProvider.otherwise('/app/home/tipidirifiuti');
});
