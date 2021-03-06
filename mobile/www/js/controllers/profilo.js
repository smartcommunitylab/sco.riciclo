angular.module('rifiuti.controllers.profilo', [])

.controller('ProfiliCtrl', function ($scope, $rootScope) {})

.controller('ModificaProfiloCtrl', function ($scope, $rootScope, $ionicNavBarDelegate, $filter, DataManager, $stateParams, $ionicPopup, $ionicModal, Profili, Raccolta, Utili) {

    $scope.search = {};

    $scope.aree = [];

    $scope.toponimi = {};

    $scope.id = $stateParams.id;

    $scope.profilo = {
        name: '',
        utenza: {},
        area: {}
    };

    $scope.updateLocations = function () {
        Raccolta.areeForTipoUtenza($scope.profilo.utenza.tipologiaUtenza).then(function (data) {
            $scope.aree = [];

            for (var i = 0; i < data.length; i++) {
                $scope.aree.push(data[i]);
                if ($scope.profilo.area && data[i].nome == $scope.profilo.area.nome) {
                    $scope.profilo.area = data[i];
                }
            }
        });
    };

    $scope.doSearchQuery = function(item) {
        if(!$scope.search.value){
            return item;
        }

        var searchString = $scope.search.value.toLowerCase();
        var n = searchString.length;

        if(n<3){
            $scope.toponimi = {};
            return true;
        }

        if(item.descrizione && item.etichetta && (typeof TOPONIMO_SEPARATOR != 'undefined')){
            $scope.checkToponimi(item.etichetta, searchString, item.descrizione);
        }

        if(!item.etichetta){
            return true;
        }

        var text = item.etichetta.toLowerCase();
        if(text.indexOf(searchString) != -1 || ($scope.toponimi[item.etichetta] && $scope.toponimi[item.etichetta].length > 0)) {
            return item;
        }
    };

    $scope.checkToponimi = function (etichetta, searchString, descrizione){
        var descrizioneList = Utili.getListFromString(TOPONIMO_SEPARATOR, descrizione);
        $scope.toponimi[etichetta] = [];

        for (var i = 0; i < descrizioneList.length; i++) {
            var text = descrizioneList[i];
            if(text.toLowerCase().indexOf(searchString) != -1) {
                $scope.toponimi[etichetta].push(descrizioneList[i]);
            }
        }
    }

    $scope.resetValues = function(){
        $scope.search.value = '';
        $scope.toponimi = {};
    }

    $scope.updateProfileType = function() {
        $scope.updateLocations();
        $scope.profilo.area = {};
    };

    // popola tipi di utenza e relative locations
    Profili.tipidiutenza().then(function (tipi) {
        tipi = tipi.sort(function (a, b) {
            return a.nome.localeCompare(b.nome);
        });
        $scope.tipologiaUtenza = tipi;

        // new profile
        if ($scope.id) {
//            $scope.profilo.utenza = tipi[0];
//            $scope.updateLocations();
//        } else {
            // get profile
            var p = Profili.byId($scope.id);
            if (!!p) {
                $scope.profilo = angular.copy(p);
                $scope.updateLocations();
            }
        }
        if (!!$rootScope.selectedProfile && $rootScope.selectedProfile.name == $scope.profilo.name) {
            $scope.isCurrentProfile = true;
        } else {
            $scope.isCurrentProfile = false;
        }

    });

    $scope.isCurrentProfile = true;
    $scope.editMode = !$scope.id;

    $scope.edit = function () {
        if (!$scope.editMode) {
            // edit
            $scope.editMode = true;

            for (var i = 0; i < $scope.tipologiaUtenza.length; i++) {
                if ($scope.tipologiaUtenza[i].nome === $scope.profilo.utenza.nome) {
                    $scope.profilo.utenza = $scope.tipologiaUtenza[i];
                }
            }
        } else {
            // save
            if (!!$scope.profilo.name && !!$scope.profilo.area && !!$scope.profilo.area.nome) {
                var newProfile = null;

                if (!!$scope.id) {
                    // update
                    newProfile = Profili.update($scope.id, $scope.profilo.name, $scope.profilo.utenza, $scope.profilo.area);
                } else {
                    // create
                    newProfile = Profili.add($scope.profilo.name, $scope.profilo.utenza, $scope.profilo.area);
                    $scope.back();
                    //return;
                }

                if (newProfile == null) {
                    // error: already exists
                    var popup = $ionicPopup.show({
                        title: '<b class="popup-title">'+$filter("translate")("warning")+'<b/>',
                        template: $filter("translate")("profile_name_already_used"),
                        buttons: [
                            {
                                text: 'OK'
                            }
                        ]
                    });
                } else {
                    $scope.editMode = false;
                }
            } else {
                // not complete
                $ionicPopup.show({
                    title: '<b class="popup-title">'+$filter("translate")("warning")+'<b/>',
                    template: 'Per completare il tuo profilo devi scegliere un nome, tipo di utenza, e una località!',
                    scope: $scope,
                    buttons: [
                        {
                            text: 'OK'
                        }
                    ]
                });
            }
        }
    };

    $scope.helpTipi = function (area) {
        var popup = $ionicPopup.show({
            title: '<b class="popup-title">Info<b/>',
            templateUrl: 'templates/profiloHelp.html',
            cssClass: 'popup-tipi',
            scope: $scope,
            buttons: [
                {
                    text: $filter("translate")("close")
                }
            ]
        });
        return;
    };

    $scope.help = function (area) {
        var popup = $ionicPopup.show({
            title: '<b class="popup-title">' + area.etichetta + '<b/>',
            //templateUrl: 'templates/profiloHelp.html',
            template: area.descrizione,
            scope: $scope,
            buttons: [
                {
                    text: $filter("translate")("close")
                }
            ]
        });
        return;
    };

    $scope.click = function () {
        if ($scope.isCurrentProfile) {
            var popup = $ionicPopup.show({
                title: '<b class="popup-title">'+$filter("translate")("alert")+'<b/>',
                template: $filter("translate")("cannot_delete_profile_in_use"),
                scope: $scope,
                buttons: [
                    {
                        text: 'OK'
                    }
                ]
            });
            return;
        }

        var popup = $ionicPopup.show({
            title: '<b class="popup-title">'+$filter("translate")("alert")+'<b/>',
            template: $filter("translate")("push_ok_to_delete"),
            //TODO: le note dovrebbero essere legate al profilo??? ora non lo sono!
            //      template: 'Premendo OK cancellerai definitivamente questo profilo, incluse tutte le eventuali note personali. Confermi?',
            scope: $scope,
            buttons: [
                {
                    text: $filter('translate')('cancel')
                },
                {
                    text: 'OK',
                    onTap: function (e) {
                        return true;
                    }
                }
            ]
        }).then(function (res) {
            if (!!res) {
                Profili.remove($scope.id);
                $scope.back();
            }
        });
    };

    $scope.modal = null;

    $scope.closeModal = function () {
        $scope.modal.hide();
    };

    /* LOCALITA SELECTOR */
    var modalLocalita = null;
    $ionicModal.fromTemplateUrl('templates/localitaModal.html', {
        scope: $scope,
        animation: 'slide-in-up'
    }).then(function (modal) {
        modalLocalita = modal;
    });

    $scope.openLocalitaSelector = function () {
        if (!$scope.profilo.utenza || !$scope.profilo.utenza.tipologiaUtenza) return;
        $scope.modal = modalLocalita;
        $scope.modal.show();
    };

    $scope.localitaSelected = function (item) {
        $scope.profilo.area = item;
        $scope.closeModal();
    };
    $scope.closeLocalitaSelector = function() {
        $scope.closeModal();
    };

    /* TIPI UTENZE MODAL */
    var modalUtenze = null;
    $ionicModal.fromTemplateUrl('templates/utenzeModal.html', {
        scope: $scope,
        animation: 'slide-in-up'
    }).then(function (modal) {
        modalUtenze = modal;
    });

    $scope.openUtenzeModal = function () {
        $scope.modal = modalUtenze;
        $scope.modal.show();
    };

    //Cleanup the modal when we're done with it!
    $scope.$on('$destroy', function () {
        if ($scope.modal) $scope.modal.remove();
    });

    // Execute action on hide modal
    $scope.$on('modal.hidden', function () {
        // Execute action
    });

    // Execute action on remove modal
    $scope.$on('modal.removed', function () {
        // Execute action
    });
});
