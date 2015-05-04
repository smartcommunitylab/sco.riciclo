angular.module('rifiuti.controllers.profilo', [])

.controller('ProfiliCtrl', function ($scope, $rootScope) {})

.controller('ModificaProfiloCtrl', function ($scope, $rootScope, $ionicNavBarDelegate, $filter, DataManager, $stateParams, $ionicPopup, $ionicModal, Profili, Raccolta) {
    $scope.aree = [];

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

    // popola tipi di utenza e relative locations
    Profili.tipidiutenza().then(function (tipi) {
        tipi = tipi.sort(function (a, b) {
            return a.nome.localeCompare(b.nome);
        });
        $scope.tipologiaUtenza = tipi;

        // new profile
        if (!$scope.id) {
            $scope.profilo.utenza = tipi[0];
            $scope.updateLocations();
        } else {
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
                    return;
                }

                if (newProfile == null) {
                    // error: already exists
                    var popup = $ionicPopup.show({
                        title: '<b class="popup-title">Attenzione !<b/>',
                        template: 'Il nome del profilo è già in uso!',
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
                    title: '<b class="popup-title">Attenzione !<b/>',
                    template: 'Per completare il tuo profilo devi scegliere un nome e una località!',
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
            scope: $scope,
            buttons: [
                {
                    text: 'Chiudi'
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
                    text: 'Chiudi'
                }
            ]
        });
        return;
    };

    $scope.click = function () {
        if ($scope.isCurrentProfile) {
            var popup = $ionicPopup.show({
                title: '<b class="popup-title">Avviso<b/>',
                template: 'Non è possibile cancellare il profilo in uso.',
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
            title: '<b class="popup-title">Avviso<b/>',
            template: 'Premendo OK cancellerai definitivamente questo profilo. Confermi?',
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


    /* LOCALITA SELECTOR */
    $ionicModal.fromTemplateUrl('templates/localitaModal.html', {
        scope: $scope,
        animation: 'slide-in-up'
    }).then(function (modal) {
        $scope.localitaModal = modal;
    });

    $scope.openLocalitaSelector = function () {
        $scope.localitaModal.show();
    };

    $scope.closeLocalitaSelector = function () {
        $scope.localitaModal.hide();
    };

    $scope.localitaSelected = function (item) {
        $scope.profilo.area = item;
        $scope.closeLocalitaSelector();
    };

    //Cleanup the modal when we're done with it!
    $scope.$on('$destroy', function () {
        $scope.localitaModal.remove();
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
