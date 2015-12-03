/**
 * ==================  angular-ios9-uiwebview.patch.js v1.1.1 ==================
 *
 * This patch works around iOS9 UIWebView regression that causes infinite digest
 * errors in Angular.
 *
 * The patch can be applied to Angular 1.2.0 – 1.4.5. Newer versions of Angular
 * have the workaround baked in.
 *
 * To apply this patch load/bundle this file with your application and add a
 * dependency on the "ngIOS9UIWebViewPatch" module to your main app module.
 *
 * For example:
 *
 * ```
 * angular.module('myApp', ['ngRoute'])`
 * ```
 *
 * becomes
 *
 * ```
 * angular.module('myApp', ['ngRoute', 'ngIOS9UIWebViewPatch'])
 * ```
 *
 *
 * More info:
 * - https://openradar.appspot.com/22186109
 * - https://github.com/angular/angular.js/issues/12241
 * - https://github.com/driftyco/ionic/issues/4082
 *
 *
 * @license AngularJS
 * (c) 2010-2015 Google, Inc. http://angularjs.org
 * License: MIT
 */

angular.module('ngIOS9UIWebViewPatch', ['ng']).config(['$provide', function($provide) {
  'use strict';

  $provide.decorator('$browser', ['$delegate', '$window', function($delegate, $window) {

    if (isIOS9UIWebView($window.navigator.userAgent)) {
      return applyIOS9Shim($delegate);
    }

    return $delegate;

    function isIOS9UIWebView(userAgent) {
      return /(iPhone|iPad|iPod).* OS 9_\d/.test(userAgent) && !/Version\/9\./.test(userAgent);
    }

    function applyIOS9Shim(browser) {
      var pendingLocationUrl = null;
      var originalUrlFn= browser.url;

      browser.url = function() {
        if (arguments.length) {
          pendingLocationUrl = arguments[0];
          return originalUrlFn.apply(browser, arguments);
        }

        return pendingLocationUrl || originalUrlFn.apply(browser, arguments);
      };

      window.addEventListener('popstate', clearPendingLocationUrl, false);
      window.addEventListener('hashchange', clearPendingLocationUrl, false);

      function clearPendingLocationUrl() {
        pendingLocationUrl = null;
      }

      return browser;
    }
  }]);
}]);
// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.controllers' is found in controllers.js
angular.module('rifiuti', [
    'ngIOS9UIWebViewPatch',
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

.run(function ($ionicPlatform, $rootScope, $ionicNavBarDelegate, $ionicHistory, $translate, $ionicPopup, $filter, $state, Profili, Utili, GeoLocate, $cordovaSplashscreen, $ionicLoading, $cordovaNetwork, $ionicConfig, DataManager) {
    console.log('app .run!!!!!!');
    $rootScope.version = VERSION;

    $rootScope.isWebView = ionic.Platform.isWebView();
    $rootScope.isPopUp = !$rootScope.isWebView && !IF_HIDDEN_FIELDS;
    $rootScope.isHiddenFields = IF_HIDDEN_FIELDS;
    $rootScope.loadingShow = function () {
        $ionicLoading.show({
            template: '<ion-spinner></ion-spinner>'
        });
    };


    var setGlobalSettings = function() {


    }


    console.log('app check1!!!!!!');
    $rootScope.showAlert = function(link) {
        var alertPopup = $ionicPopup.alert({
            title: $filter('translate')('warning'),
            template: $filter('translate')('web_version_availability'),
            buttons: [
            {
                text: 'OK',
                type: 'button-100r'
            }
          ]
        });
   };

    $rootScope.color = function(item) {
        var colorById = DataManager.getColorById(item.colore);
        return Utili.getRGBColor(colorById);
    }

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
        if(navigator.splashscreen){
            $cordovaSplashscreen.hide();
        }
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
        console.log("ionicPlatform.ready!");
        if(navigator.connection){
            $rootScope.network = $cordovaNetwork.getNetwork();
            console.log("network: "+$rootScope.network);
            $rootScope.isOnline = $cordovaNetwork.isOnline();
            console.log("isOnline: "+$rootScope.isOnline);
        }else{
            $rootScope.network = null;
            $rootScope.isOnline = null;
        }

        $rootScope.platform = ionic.Platform;
        $rootScope.backButtonStyle = $ionicConfig.backButton.icon();

        DataManager.initGlobalSettings().then(function (result) {
            $translate.use($rootScope.globalSettings.selectedLang);
        });

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
                if(!$rootScope.globalSettings.getSelectedLang){
                    $translate.use((language.value).split("-")[0]).then(function (data) {
                        console.log("SUCCESS -> " + data);
                    }, function (error) {
                        console.log("ERROR -> " + error);
                    });
                }else{
                    $translate.use($rootScope.globalSettings.getSelectedLang);
                }
            }, null);
        }


        var profiles = DataManager.getProfiles();
        if (profiles && profiles.length > 0) {
            Profili.updateNotifications();
        }


        document.addEventListener("deviceready", function () {
          //console.log('removing splashscreen...');
          //giving another couple of seconds to ui to complete css&font elements redraw (on android)
          setTimeout(function(){ navigator.splashscreen.hide(); },1500);
        });
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

    $translateProvider.translations("en", ENGLISH_VERSION);

    $translateProvider.translations("it", ITALIAN_VERSION);
    $translateProvider.preferredLanguage(current_lang);

    //debug only
    //$translateProvider.preferredLanguage("en"); // solo lingua inglese, commentare per attivare il riconoscimento della lingua automatico
    //end debug only

    $translateProvider.fallbackLanguage("it");

    $stateProvider
        .state('app', {
            url: "/app",
            cache: false,
            abstract: true,
            templateUrl: "templates/menu.html",
            controller: 'AppCtrl'
        })

    .state('app.home', {
        url: "/home",
  //      abstract: true,
        cache: false,
        views: {
            'menuContent': {
                templateUrl: "templates/home.html",
                controller: 'HomeCtrl'
            }
        }
    })

    .state('app.home.note', {
        url: "/note",
        cache: false,
        views: {
            'note': {
                templateUrl: "templates/home/note.html",
                controller: 'noteCtrl'
            }
        }
    })

    .state('app.home.news', {
        url: "/news",
        cache: false,
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
        cache: false,
        views: {
            'tipidirifiuti': {
                templateUrl: "templates/home/tipidirifiuti.html",
                controller: 'tipidirifiutiCtrl'
            }
        }
    })

    .state('app.home.calendario', {
        url: "/calendario",
        cache: false,
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
        cache: false,
        views: {
            'menuContent': {
                templateUrl: "templates/tipiDiRaccolta.html",
                controller: 'TDRCtrl'
            }
        }
    })

    .state('app.raccolta', {
        url: "/raccolta/:id",
        cache: false,
        views: {
            'menuContent': {
                templateUrl: "templates/raccolta.html",
                controller: 'RaccoltaCtrl'
            }
        }
    })

    .state('app.rifiuti', {
        url: "/rifiuti/:tipo",
        cache: false,
        views: {
            'menuContent': {
                templateUrl: "templates/rifiuti.html",
                controller: 'RifiutiCtrl'
            }
        }
    })

    .state('app.rifiuto', {
        url: "/rifiuto/:nome",
        cache: false,
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

    .state('app.infoRaccolta', {
        url: "/infoRaccolta/:id",
        cache: false,
        views: {
            'menuContent': {
                templateUrl: "templates/infoRaccolta.html",
                controller: 'InfoRaccoltaCtrl'
            }
        }
    })

    //.state('app.profili', {
    //    url: "/profili",
    //    views: {
    //        'menuContent': {
    //            templateUrl: "templates/profili.html",
    //            controller: 'ProfiliCtrl'
    //        }
    //    }
    //})

    .state('app.profili', {
        url: "/profili",
        views: {
            'menuContent': {
                templateUrl: "templates/profiliUnique.html",
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

    .state('app.aggProfiloUnique', {
        url: "/aggProfiloUnique",
        views: {
            'menuContent': {
                templateUrl: "templates/modificaProfiloUnique.html",
                controller: 'ModificaProfiloUniqueCtrl'
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

    .state('app.modificaProfiloUnique', {
        url: "/modificaProfiloUnique/:id",
        views: {
            'menuContent': {
                templateUrl: "templates/modificaProfiloUnique.html",
                controller: 'ModificaProfiloUniqueCtrl'
            }
        }
    })

    .state('app.segnala', {
        url: "/segnala",
        cache: false,
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
        cache: false,
        views: {
            'menuContent': {
                templateUrl: "templates/menunote.html",
                controller: 'noteCtrl'
            }
        }
    })

    .state('app.contatti', {
        url: "/contatti",
        cache: false,
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
