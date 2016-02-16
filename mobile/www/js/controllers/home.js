angular.module('rifiuti.controllers.home', [])

.controller('HomeCtrl', function ($scope, $rootScope, $ionicSideMenuDelegate, DataManager, $ionicLoading, $ionicScrollDelegate, Conf) {
   // $rootScope.noteSelected = false;
    $scope.showNews = Conf.showNews;

    $scope.height = window.innerHeight;
    $scope.width = window.innerWidth;

    $scope.titleText = function () {
//        if (!$rootScope.noteSelected) {
            return APP_NAME;
//        } else {
//            return '';
//        }
    };

    $scope.subTitleText = function () {
//        if (!$rootScope.noteSelected) {
            return ($scope.selectedProfile ? $scope.selectedProfile.name : '');
//        } else {
//            return '';
//        }
    };

    $scope.leftClick = function () {
//        if (!$rootScope.noteSelected) {
            $ionicSideMenuDelegate.toggleLeft();
//        } else {
//            $rootScope.noteSelected = false;
//        }
    };

    $scope.oneInThree = function (v) {
        var f = [];
        for (var i = 0; i < v.length; i = i + 3) {
            f[i / 3] = v[i];
        }
        return f;
    };

    $scope.reset = function () {
        alert("Resetting!");
        localStorage.clear();
    };

    $rootScope.showTutorial = false;
    var stringTutorial = DataManager.getTutorial();

    if (stringTutorial == "false" || !!$rootScope.promptedToProfile) {
        $rootScope.showTutorial = false;
    } else {
        $rootScope.showTutorial = true;
    }

    $scope.stopTutorial = function () {
        DataManager.saveTutorial("false");
    };

    $scope.show = function () {
        if (!!!$rootScope.showTutorial) {
            return;
        }
        $ionicScrollDelegate.scrollTop();
        $ionicLoading.show({
            templateUrl: 'templates/tutorial.html',
        });
        $rootScope.showTutorial = false;
        $scope.stopTutorial();
    };

    $rootScope.$watch('showTutorial', function (newValue, oldValue) {
        if (!!newValue) {
            $scope.show();
        }
    });
})

.controller('newsCtrl', function ($scope, $rootScope, FeedService) {

    $rootScope.loadingShow();
    FeedService.load(FEED_URL,APP_ID).then(function(entries) {
        entries.forEach(function(entry) {
            entry.dateTime = new Date(entry.publishedDate);
        });
        $scope.entries = entries;
        $rootScope.loadingHide();
    });

    $scope.doRefresh = function() {
        FeedService.load(FEED_URL,APP_ID, true).then(function(entries) {
            entries.forEach(function(entry) {
                entry.dateTime = new Date(entry.publishedDate);
            });
            $scope.entries = entries;
            $scope.$broadcast('scroll.refreshComplete');
        });
    };

})
.controller('newsItemCtrl', function ($scope, $stateParams, $rootScope, FeedService) {
    $scope.subTitleText = function () {
        return ($scope.selectedProfile ? $scope.selectedProfile.name : '');
    };
    $scope.newsItem = null;
    $scope.idx = $stateParams.id;
    FeedService.getByIdx($scope.idx, FEED_URL,APP_ID).then(function(data){
        $scope.newsItem = data;
        $scope.newsItem.dateTime = new Date($scope.newsItem.publishedDate);
    });
})

.controller('noteCtrl', function ($scope, $rootScope, $ionicPopup, $filter, Profili) {
    $scope.noteSelected = false;

    $scope.variableIMG = "riciclo-add";
    var updateIMG = function () {
        $scope.variableIMG = !$scope.noteSelected ? "riciclo-add" : "riciclo-discard";
    };

//    $rootScope.$watch('noteSelected', function () {
//        if (!$scope.noteSelected) {
//            $scope.selectedNotes = [];
//            $scope.multipleNoteSelected = false;
//        }
//        updateIMG();
//    });

    var init = function () {
        $scope.notes = Profili.getNotes();
        $scope.selectedNotes = [];
        $scope.multipleNoteSelected = false;
    };

    $rootScope.$watch('selectedProfile', function (a, b) {
        if (b == null || a.id != b.id) {
            init();
        }
    });

    init();

    $scope.addNote = function (nota) {
        $scope.notes = Profili.addNote(nota);
    };

    $scope.removeNotes = function (idx) {
        $ionicPopup.show(popupDelete()).then(function (res) {
            if (res) {
                $scope.notes = Profili.deleteNotes(idx);
                $scope.selectedNotes = [];
                $scope.multipleNoteSelected = false;
                $scope.noteSelected = false;
                updateIMG();
            }
        });
    };

    $scope.noteSelect = function (idx) {
        var p = $scope.selectedNotes.indexOf(idx);
        if (p == -1) {
            $scope.selectedNotes.push(idx);
            $scope.noteSelected = true;
            if ($scope.selectedNotes.length > 1) $scope.multipleNoteSelected = true;
        } else {
            $scope.selectedNotes.splice(p, 1);
            if ($scope.selectedNotes.length <= 1) {
                $scope.multipleNoteSelected = false;
                if ($scope.selectedNotes.length < 1) $scope.noteSelected = false;
            }
        }
        updateIMG();
    };

    var popupDelete = function () {
        return {
            template: $scope.multipleNoteSelected ? $filter('translate')('delete_confirmation_multi') : $filter('translate')('delete_confirmation'),
            title: $filter('translate')('alert'),
            scope: $scope,
            buttons: [
                {
                    text: $filter('translate')('cancel')
                },
                {
                    text: $filter('translate')('confirm'),
                    onTap: function (e) {
                        return true;
                    }
          }
        ]
        }
    };
    var popupCreate = function () {
        return {
            template: '<textarea class="form-control" rows="5" id="comment" ng-model="data.nota">',
            title: $filter('translate')('what_remember'),
            scope: $scope,
            buttons: [
                {
                    text: $filter('translate')('cancel')
              },
                {
                    text: '<b>' + $filter('translate')('save') + '</b>',
                    type: 'button-100r',
                    onTap: function (e) {
                        if (!$scope.data.nota) {
                            return null;
                        } else {
                            return $scope.data.nota;
                        }
                    }
              }
          ]
        };
    };
    $scope.click = function () {
        if ($scope.noteSelected) {
            $scope.removeNotes($scope.selectedNotes);
        } else {
            $scope.data = {};
            $ionicPopup.show(popupCreate()).then(function (res) {
                if (res != null && res != undefined) {
                    $scope.addNote(res);
                    $scope.selectedNotes = [];
                    $scope.multipleNoteSelected = false;
                    $scope.noteSelected = false;
                    updateIMG();
                }
            });
        }
    };

    $scope.edit = function () {
        $scope.data = {
            'nota': $scope.notes[$scope.selectedNotes[0]],
            idx: $scope.selectedNotes[0]
        };

        var popup = $ionicPopup.show(popupCreate()).then(function (res) {
            if (res != null && res != undefined) {
                $scope.notes = Profili.updateNote($scope.data.idx, res);
                $scope.selectedNotes = [];
                $scope.multipleNoteSelected = false;
                $scope.noteSelected = false;
                updateIMG();
            }
        });
    };
})

.controller('calendarioCtrl', function ($scope, $rootScope, $ionicScrollDelegate, $location, Calendar, Utili, $timeout, $filter, $document, DataManager) {
    $scope.calendarView = false;

	$location.hash = function(val) {
		if (!!val) {
			window._globalscrollid = val;
		}
		return window._globalscrollid;
	};

    $scope.noteSelected = false;

    $scope.switchView = function () {
        $scope.calendarView = !$scope.calendarView;
        $scope.updateIMG2();
        if ($scope.calendarView) {
            $scope.doScroll = function() {
                $ionicScrollDelegate.scrollTop();
            }
        } else {
                $ionicScrollDelegate.scrollTop();
        }
    }

    $scope.selectDay = function (i) {
        if (i.colors.length == 0) return;
        $rootScope.loadingShow();
        $scope.currListItem = i;
        $scope.showDate = i.date;

        $scope.doScroll = function() {
            $ionicScrollDelegate.scrollBottom();
            $location.hash('id' + i.date.getTime());
            $ionicScrollDelegate.anchorScroll(true);
        };
        $scope.daySubList = Calendar.toWeek($scope.dayList, $scope.showDate, $scope.daySubListRunningEnd);
        $rootScope.loadingHide();

//        $timeout(function () {
//            $ionicScrollDelegate.scrollTop();
//            $location.hash('id' + i.date.getTime());
//            $ionicScrollDelegate.anchorScroll(true);
//        }, 200);

        $scope.calendarView = !$scope.calendarView;
        $scope.updateIMG2();
    }

    $scope.variableIMG2 = "riciclo-calendar_list";

    $scope.updateIMG2 = function () {
        $scope.variableIMG2 = $scope.calendarView ? "riciclo-calendar_table" : "riciclo-calendar_list";
    };

    $scope.firstDayIndex = function (week) {
        return Utili.DOWTextToDOW(week[0].day);
    };

    $scope.lastDayIndex = function (week) {
        return Utili.DOWTextToDOW(week[week.length - 1].day);
    };

    $scope.getEmptyArrayByLength = function (length) {
        var array = [];
        if (length > 100) length = 100;
        for (var i = 0; i < length; i++) {
            array.push(i - 1);
        }
        return array;
    };

    var scrollToday = function (apply) {
        // TODO
        //return;

        if ($scope.calendarView == true) {
            $scope.doScroll = function() {
                var time = 0;
                var i = 0;
                while (i < $scope.dayList.length && time < $scope.currDate.getTime()) {
                    time = $scope.dayList[i++].date.getTime();
                }
                if (i - 2 >= 0 && i - 2 <= $scope.dayList.length) {
                    $ionicScrollDelegate.scrollBottom();
                    $location.hash('id' + $scope.dayList[i - 2].date.getTime());
                    $scope.currListItem = $scope.dayList[i - 2];
                    //window._globalscrollid = 'id' + $scope.currListItem.date.getTime();
                    $ionicScrollDelegate.anchorScroll(true);
                } else {
                    $ionicScrollDelegate.scrollTop();
                }
            };
            if (apply) {
              $scope.doScroll();
            }
        }
    };

    var buildMonthData = function (gotoday) {
        if (!$scope.month || $scope.month.name != Utili.monthYear($scope.showDate.getMonth(), $scope.showDate.getFullYear())) {
            $scope.loaded = false;
            Calendar.fillWeeks($scope.showDate, $rootScope.selectedProfile.utenza.tipologiaUtenza, $rootScope.selectedProfile.aree).then(function (data) {
                $scope.colorLegendMap = data["colorLegendMap"];
                $scope.month = {
                    name: Utili.monthYear($scope.showDate.getMonth(), $scope.showDate.getFullYear()),
                    weeks: data["weeks"]
                };
                $scope.dayList = Calendar.toListData($scope.month.weeks);
                $scope.daySubListRunningEnd = null;
                $scope.daySubList = Calendar.toWeek($scope.dayList, $scope.showDate, $scope.daySubListRunningEnd);

                $scope.loaded = true;
                if (gotoday) {
                    scrollToday();
                }
            });
        } else {
            $scope.daySubList = Calendar.toWeek($scope.dayList, $scope.showDate, $scope.daySubListRunningEnd);
            if (gotoday) {
                scrollToday(true);
            }
        }
    };

    var init = function () {
        $scope.month = {};
        $scope.calendarView = false;
        $scope.loaded = false;
        $scope.currDate = new Date();
        $scope.currListItem = null;
        $scope.daySubList = null;
        $scope.dayList = []; //$scope.getEmptyArrayByLength(Calendar.dayArrayHorizon($scope.currDate.getFullYear(),$scope.currDate.getMonth(), $scope.currDate.getDate()));
        $scope.showDate = new Date();
        $scope.daySubListRunningEnd = null;
        buildMonthData();
    }

    init();

    $rootScope.$watch('selectedProfile', function (a, b) {
        if (b == null || a.id != b.id) {
            init();
        }
    });

    $scope.fullDate = function (d) {
            return Utili.fullDateFormat(d, $filter('translate'));
        }
        /*$scope.$watch('month', function (a, b) {
            if (a != null && (b == null || a.name !== b.name || $scope.dayList.length == 0)) {
                $scope.dayList = Calendar.toListData($scope.month.weeks);
                $scope.dayListLastMonth = Utili.lastDateOfMonth($scope.showDate);
            }
        });*/

    $scope.loadMoreDays = function () {
        if ($scope.daySubListRunningEnd <= $scope.dayList.length) {
            $scope.daySubListRunningEnd += 7;
            $scope.daySubList = Calendar.toWeek($scope.dayList, $scope.showDate, $scope.daySubListRunningEnd);
        }
        $scope.$broadcast('scroll.infiniteScrollComplete');
    };

    $scope.getColor = function (colorString) {
        var colorById = DataManager.getColorById(colorString);
        return Utili.getRGBColor(colorById);
        //return Utili.getRGBColor(colorString);
    };

    $scope.getIcon = function (item) {
        var colorById = DataManager.getColorById(item.colore);
        //var tipoPuntoRaccolta = DataManager.getCategoriaById('tipologiaPuntiRaccolta',item.tipologiaPuntiRaccolta);
        var icona = DataManager.getIconById(item.tipologiaPuntiRaccolta);
        return Utili.icon(icona, colorById);
    }

    $scope.goToToday = function () {
        $scope.showDate = new Date();
        buildMonthData(true);
    };

    $scope.nextMonth = function () {
        $scope.showDate.setDate(1);
        $scope.showDate.setMonth($scope.showDate.getMonth() + 1);
        buildMonthData();
    };

    $scope.lastMonth = function () {
        $scope.showDate.setDate(1);
        $scope.showDate.setMonth($scope.showDate.getMonth() - 1);
        buildMonthData();
    };

})
