angular.module('rifiuti.controllers.common', [])

.controller('AppCtrl', function ($scope, $rootScope, $location, Profili, DataManager, Conf) {
    $scope.app_name = APP_NAME;

    $scope.showTutorial = function () {
        $rootScope.showTutorial = true;
    };

    $scope.showNews = function () {
        return Conf.showNews();
    };

    DataManager.checkVersion($rootScope.profili).then(function () {
        $rootScope.loadingHide();
    });

    //localStorage.removeItem('profiles');
    if (!localStorage.profiles || localStorage.profiles.length == 0) {
        $rootScope.promptedToProfile = true;
        $location.path("app/aggProfilo");
    } else {
        Profili.read();
        Profili.select(Profili.selectedProfileIndex());
        Profili.updateNotifications();
    }
})

.controller('InfoCtrl', function ($scope) {})

.controller('SegnalaCtrl', function ($scope, $rootScope, $cordovaCamera, Raccolta) {

    //    $scope.GPScoords = null;
    //    var GPScoordsTmp = null;
    //
    //    var posizioneG = function () {
    //        //navigator.geolocation.getCurrentPosition(success);
    //        //if (navigator.geolocation) {
    //        navigator.geolocation.getCurrentPosition(function (position) {
    //            //alert("your position is: " + position.coords.latitude + ", " + position.coords.longitude);
    //            GPScoordsTmp = "[ " + position.coords.latitude + ", " + position.coords.longitude + " ]";
    //            $scope.GPScoords = GPScoordsTmp;
    //        });
    //        // } else {
    //        //  showError("Your browser does not support Geolocation!");
    //        // }
    //    };

    $scope.checked = true;
    $scope.checkboxImage = "img/rifiuti_btn_check_on_holo_light.png";
    $scope.msg = {
        text: null
    };

    $scope.signal = {
        selectedTipoSegnalazione: null
    };

    $scope.tipiSegnalazioni = [];

    Raccolta.segnalazioni($scope.selectedProfile.aree).then(function (data) {
        $scope.tipiSegnalazioni = data;
        if (data.length >= 1) $scope.signal.selectedTipoSegnalazione = data[0];
    });

    $scope.takePicture = function () {
        var options = {
            quality: 75,
            destinationType: Camera.DestinationType.FILE_URI, //Camera.DestinationType.DATA_URL,
            sourceType: Camera.PictureSourceType.CAMERA,
            allowEdit: false,
            encodingType: Camera.EncodingType.JPEG,
            targetWidth: 150,
            targetHeight: 150,
            popoverOptions: CameraPopoverOptions,
            saveToPhotoAlbum: false
        };

        $cordovaCamera.getPicture(options).then(function (imageData) {
            $scope.imgURI = imageData; //"data:image/jpeg;base64," + imageData;
        }, function (err) {
            // An error occured. Show a message to the user
        });
    }

    $scope.toggleCheck = function () {
        //$scope.posizioneG();
        $scope.checked = !$scope.checked;
        $scope.checkboxImage = $scope.checked ? "img/rifiuti_btn_check_on_holo_light.png" : "img/rifiuti_btn_check_off_holo_light.png";
        $scope.GPScoords = $scope.checked ? GPScoordsTmp : "";
    };

    $scope.sendEmail = function () {
        var emailPlugin = null;
        if (ionic.Platform.isWebView()) {
            if (window.plugin.email) emailPlugin = window.plugin.email;
            else if (cordova.plugins.email) emailPlugin = cordova.plugins.email;
        }
        if (emailPlugin) {
            var body = $scope.msg.text ? ($scope.msg.text + ' ') : '';
            body += $scope.checked ? $scope.GPScoords : '';
            window.plugin.email.open({
                to: [$scope.signal.selectedTipoSegnalazione.email],
                subject: "segnalazione dalla app '" + APP_NAME + "'", // subject of the email
                body: [body],
                isHtml: false,
                attachments: $scope.imgURI
                    //        attachment: $scope.imgURI ? "base64:icon.png//" + $scope.imgURI.substring(26) : null
            });
        } else {
            //console.log('using "mailto:" schema in location...');
            window.open("mailto:" + $scope.signal.selectedTipoSegnalazione.email, "_system");
        }
    };

    if ($rootScope.myPosition) {
        $scope.GPScoords = "[ " + $rootScope.myPosition.join(', ') + " ]";
    }
    //    posizioneG();

})

.controller('SettingsCtrl', function ($scope, $rootScope, $ionicScrollDelegate, Raccolta, Profili) {
    /*$scope.mainScrollResize = function () {
        $ionicScrollDelegate.$getByHandle('mainScroll').resize();
    }*/

    $scope.papTypes = $rootScope.selectedProfile.PaP;
    $scope.settings = $rootScope.selectedProfile.settings;

    $scope.timepickerSlots = {
        format: 24,
        step: 5
    };

    $scope.saveSettings = function () {
        Profili.saveAll();
    };

    $rootScope.$watch('selectedProfile', function (a, b) {
        if (b == null || a.id != b.id) {
            $scope.papTypes = a.PaP;
            $scope.settings = a.settings;
        }
    });
})

.controller('ContattiCtrl', function ($scope, $ionicScrollDelegate, Raccolta) {
    Raccolta.contatti().then(function (data) {
        $scope.contatti = data;
    });

    $scope.mainScrollResize = function () {
        $ionicScrollDelegate.$getByHandle('mainScroll').resize();
    }
})

.controller('ContattoCtrl', function ($scope, $stateParams, $ionicScrollDelegate, Raccolta) {
    Raccolta.contatti().then(function (data) {
        $scope.contatto = data[$stateParams.id];
        if (!!$scope.contatto.sitoIstituzionale && $scope.contatto.sitoIstituzionale.indexOf('http') != 0) {
            $scope.contatto.sitoIstituzionale = 'http://' + $scope.contatto.sitoIstituzionale;
        }
        if (!!$scope.contatto.sitoWeb && $scope.contatto.sitoWeb.indexOf('http') != 0) {
            $scope.contatto.sitoWeb = 'http://' + $scope.contatto.sitoWeb;
        }
    });

    $scope.mainScrollResize = function () {
        $ionicScrollDelegate.$getByHandle('mainScroll').resize();
    }
})

.controller('TutorialCtrl', function ($scope, $ionicLoading) {

    $scope.close = function () {
        $ionicLoading.hide();
    };

    $scope.index = 0;

    $scope.next = function () {
        if ($scope.tutorial[$scope.index].skippable) {
            $scope.index++;
        } else {
            $scope.close();
        }
    };

    var findElement = function (id, eclass, eidx) {
        if (id) {
            return document.getElementById(id);
        } else {
            var arr = document.getElementsByClassName(eclass);
            var idx = eidx ? eidx : 0;
            if (arr.length > 0) {
                for (var i = 0; i < arr.length; i++) {
                    if (arr[i].clientWidth > 0 && arr[i].clientHeight > 0) {
                        if (idx == 0) return arr[i];
                        else idx--;
                    }
                }
            }
            return null;
        }
    }

    var getX = function (id, eclass, eidx) {
        var div = findElement(id, eclass, eidx);
        if (!div) return 0;
        var rect = div.getBoundingClientRect();
        return rect.left + 0.5 * (rect.right - rect.left);
        var width = window.innerWidth;
    };

    var getY = function (id, eclass, eidx) {
        var div = findElement(id, eclass, eidx);
        if (!div) return 0;
        var rect = div.getBoundingClientRect();
        return rect.top + 0.5 * (rect.bottom - rect.top);
    };

    $scope.tutorial = [
        {
            index: 1,
            primo: 44,
            title: "TTUno",
            x: 3,
            y: 40,
            text: "TutorialUno",
            imgX: function () {
                //                var width = window.innerWidth;
                //                return width - 80
                return getX("searchButton") - 48
            }, //getX("searchButton")-320},
            imgY: function () {
                return getY("searchButton") - 48
            },
            skippable: true
        },
        {
            index: 1,
            title: "TTDue",
            x: 3, //3
            y: 40,
            text: "TutorialDue",
            imgX: function () {
                return getX("rifiutoId") - 48
            },
            imgY: function () {
                return getY("rifiutoId") - 48
            },
            skippable: true
        },
        {
            index: 1,
            title: SHOW_NEWS ? "TTNews": "TTTre",
            x: 3,
            y: 40,
            text: SHOW_NEWS ? "TutorialNews" : "TutorialTre",
            imgX: function () {
                return getX(null, "tab-item", 0) - 48
            },
            imgY: function () {
                return getY(null, "tab-item", 0) - 48
            },
            skippable: true
        },
        {
            index: 1,
            title: "TTQuattro",
            x: 3,
            y: 40,
            text: "TutorialQuattro",
            imgX: function () {
                //var width = window.innerWidth;
                //return 0.5 * width + 80
                return getX(null, "tab-item", 2) - 48
            }, //return getX("calendarioId")+305},
            imgY: function () {
                return getY(null, "tab-item", 2) - 48
            },
            skippable: true
        },
        {
            index: 1,
            title: "TTCinque",
            x: 3,
            y: 40,
            text: "TutorialCinque",
            imgX: function () {
                return getX(null, "left-buttons") - 48
            },
            imgY: function () {
                return getY(null, "left-buttons") - 48
            },
            skippable: false
        }
    ];
});