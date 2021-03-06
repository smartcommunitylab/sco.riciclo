var calendariraccoltaApp = angular.module('calendariraccolta', ['DataService']);

var calendariraccoltaCtrl = calendariraccoltaApp.controller('userCtrl', function($scope, $http, DataService) {
	DataService.getProfile().then(
	function(p) {
		$scope.initData(p);
	},
	function(e) {
		console.log(e);
		$scope.error = true;
		$scope.errorMsg = e.errorMsg;
	});

	$scope.selectedMenu = "relazioni";
	$scope.selectedTab = "menu-calendariraccolta";
	$scope.language = "it";
	$scope.defaultLang = "it";
	$scope.draft = true;

	$scope.edit = false;
	$scope.create = false;
	$scope.view = false;
	$scope.search = "";
	$scope.incomplete = true;
	$scope.timetableIncomplete = true;
	$scope.timetableError = false;

	$scope.error = false;
	$scope.errorMsg = "";

	$scope.ok = false;
	$scope.okMsg = "";

	$scope.data = null;
	$scope.status = 200;

	$scope.selectedArea = null;
	$scope.selectedTipologiaPuntoRaccolta =  null;
	$scope.selectedTipologiaUtenza = null;

	$scope.areaList = [];
	$scope.tipologiaUtenzaList = [];
	$scope.tipologiaPuntoRaccoltaList = [];
	$scope.calendarioRaccoltaList = [];

	$scope.areaNameMap = {};
	$scope.tipologiaPuntoRaccoltaNameMap = {};

	$scope.timetableList = [];

	$scope.fId = "";

	$scope.fDateFrom = "";
	$scope.fDateTo = "";
	$scope.fHourFrom = "";
	$scope.fHourTo = "";

	$scope.fDateDayOfWeek = "";
	$scope.fDateWorkingDay = "";
	$scope.fDateWorkingDayList = [];
	$scope.fDateWorkingDaySelected = "";

	$scope.fDateExceptionDay = "";
	$scope.fDateExceptionDayList = [];
	$scope.fDateExceptionDaySelected = "";

	$scope.fDateNotes = "";
	$scope.timetableDateNotes = {};

	$scope.itemToDelete = "";

	$scope.initData = function(profile) {
		$scope.profile = profile;

		var urlTipologiaUtenza = "api/tipologia/utenza/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaUtenza, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.tipologiaUtenzaList = response.data;
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
		});

		var urlTipologiaPuntoRaccolta = "api/tipologia/puntoraccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaPuntoRaccolta, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			for (var d = 0, len = response.data.length; d < len; d += 1) {
				var element = response.data[d];
				if(element.type == "PP") {
					$scope.tipologiaPuntoRaccoltaList.push(element);
				}
			}
			$scope.tipologiaPuntoRaccoltaNameMap = $scope.setLocalNameMap($scope.tipologiaPuntoRaccoltaList);
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
		});

		var urlArea = "api/area/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlArea, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.areaList = response.data;
			$scope.areaNameMap = $scope.setNameMap($scope.areaList);
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
		});

		var urlCalendarioRaccolta = "api/calraccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlCalendarioRaccolta, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.calendarioRaccoltaList = response.data;
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
		});
	};
	
	$scope.doSearch = function(item) {
		var q = $scope.search.toLowerCase();
		var text;
		if($scope.getAreaName(item.area)) {
			text = $scope.getAreaName(item.area).toLowerCase();
			if(text.indexOf(q) != -1) {
				return true;
			}
		}
		if($scope.getTipologiaUtenzaName(item.tipologiaUtenza)) {
			text = $scope.getTipologiaUtenzaName(item.tipologiaUtenza).toLowerCase();
			if(text.indexOf(q) != -1) {
				return true;
			}
		}
		if($scope.getTipologiaPuntoRaccoltaName(item.tipologiaPuntoRaccolta)) {
			text = $scope.getTipologiaPuntoRaccoltaName(item.tipologiaPuntoRaccolta).toLowerCase();
			if(text.indexOf(q) != -1) {
				return true;
			}
		}
		return false;
	};

	$scope.getAreaName = function(id) {
		return $scope.areaNameMap[id];
	};

	$scope.getTipologiaPuntoRaccoltaName = function(id) {
		return $scope.tipologiaPuntoRaccoltaNameMap[id];
	};

	$scope.getTipologiaUtenzaName = function(id) {
		return id;
	}

	$scope.setItemToDelete = function(id) {
		$scope.itemToDelete = id;
	};

	$scope.resetError = function() {
		$scope.error = false;
		$scope.errorMsg = "";
	};

	$scope.resetOk = function() {
		$scope.ok = false;
		$scope.okMsg = "";
	};

	$scope.changeLanguage = function(language) {
		$scope.language = language;
		$scope.areaNameMap = $scope.setNameMap($scope.areaList);
		$scope.tipologiaPuntoRaccoltaNameMap = $scope.setLocalNameMap($scope.tipologiaPuntoRaccoltaList);
	};

	$scope.resetUI = function() {
		$scope.search = "";
		$scope.incomplete = false;
		$('html,body').animate({scrollTop:0},0);
	};

	$scope.resetForm = function() {
		$scope.fId = "";
		$scope.tipologiaUtenzaSearch = {};
		$scope.selectedArea = null;
		$scope.selectedTipologiaUtenza = null;
		$scope.selectedTipologiaPuntoRaccolta = null;
		$scope.itemToDelete = "";
	};

	$scope.getModalHeaderClass = function() {
		if($scope.view) {
			return "view";
		}
		if($scope.edit) {
			return "edit";
		}
		if($scope.create) {
			return "create";
		}
	};

	$scope.getActualName = function() {
		var name = $scope.getAreaName($scope.fId);
		return name;
	};

	$scope.setNote = function() {
		$scope.timetableDateNotes[$scope.language] = $scope.fDateNotes;
	};

	$scope.newItem = function() {
		$scope.edit = false;
		$scope.create = true;
		$scope.view = false;
		$scope.incomplete = true;
		$scope.resetForm();
	};

	$scope.editItem = function(id, modify) {
		if(modify) {
			$scope.edit = true;
			$scope.view = false;
		} else {
			$scope.edit = false;
			$scope.view = true;
		}
		$scope.create = false;
		var relation = $scope.findByObjectId($scope.calendarioRaccoltaList, id);
		if(relation != null) {
			$scope.fId = id;
			$scope.selectedArea = $scope.findByObjectId($scope.areaList, relation.area);
			$scope.selectedTipologiaUtenza = $scope.findByObjectId($scope.tipologiaUtenzaList, relation.tipologiaUtenza);
			$scope.selectedTipologiaPuntoRaccolta = $scope.findByObjectId($scope.tipologiaPuntoRaccoltaList, relation.tipologiaPuntoRaccolta);
			$scope.timetableList = relation.orarioApertura;
			$scope.incomplete = false;
		}
		$('html,body').animate({scrollTop:0},0);
	};

	$scope.saveItem = function() {
		if($scope.create) {
			var element = {
				area: '',
				tipologiaUtenza: '',
				tipologiaPuntoRaccolta: ''
			};
			element.area = $scope.selectedArea.objectId;
			element.tipologiaUtenza = $scope.selectedTipologiaUtenza.objectId;
			element.tipologiaPuntoRaccolta = $scope.selectedTipologiaPuntoRaccolta.objectId;

			var url = "api/calraccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
			$http.post(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
				// this callback will be called asynchronously
				// when the response is available
				$scope.status = response.status;
				$scope.data = response.data;
				$scope.ok = true;
				$scope.okMsg = "Operazione eseguita con successo";
				$scope.calendarioRaccoltaList.unshift($scope.data);
				$scope.resetUI();
				console.log("saveCalendarioRaccolta:" + response.status + " - " + response.data);
			},
			function(response) {
				// called asynchronously if an error occurs
				// or server returns response with an error status.
		  	console.log(response.data);
		  	$scope.error = true;
		  	$scope.errorMsg = response.data.errorMsg || "Request failed";
		  	$scope.status = response.status;
			});
		}
		if($scope.edit) {
			var element = $scope.findByObjectId($scope.calendarioRaccoltaList, $scope.fId);
			if(element != null) {
				element.area = $scope.selectedArea.objectId;
				element.tipologiaUtenza = $scope.selectedTipologiaUtenza.objectId;
				element.tipologiaPuntoRaccolta = $scope.selectedTipologiaPuntoRaccolta.objectId;

				var url = "api/calraccolta/" + $scope.profile.appInfo.ownerId + "/" + 
				encodeURIComponent(element.objectId) + "/?draft=" + $scope.draft;
				$http.put(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					console.log("saveCalendarioRaccolta:" + response.status + " - " + response.data);
				},
				function(response) {
					// called asynchronously if an error occurs
					// or server returns response with an error status.
			  	console.log(response.data);
			  	$scope.error = true;
			  	$scope.errorMsg = response.data.errorMsg || "Request failed";
			  	$scope.status = response.status;
				});
			}
		}
	};

	$scope.deleteItem = function() {
		var id = $scope.itemToDelete;
		var index = $scope.findIndex($scope.calendarioRaccoltaList, id);
		if(index >= 0) {
			var element = $scope.calendarioRaccoltaList[index];
			if(element != null) {
				var url = "api/calraccolta/" + $scope.profile.appInfo.ownerId + "/" + 
				encodeURIComponent(element.objectId) + "/?draft=" + $scope.draft;
				$http.delete(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					$scope.calendarioRaccoltaList.splice(index, 1);
					$scope.resetUI();
					console.log("deleteCalendarioRaccolta:" + response.status + " - " + response.data);
				},
				function(response) {
				  // called asynchronously if an error occurs
					// or server returns response with an error status.
			  	console.log(response.data);
			  	$scope.error = true;
			  	$scope.errorMsg = response.data.errorMsg || "Request failed";
			  	$scope.status = response.status;
				});
			}
		}
	};

	$scope.deleteTimetable = function(index) {
		var index = $scope.itemToDelete;
		console.log("deleteTimetable:" + index);
		var relation = $scope.findByObjectId($scope.calendarioRaccoltaList, $scope.fId);
		if(relation != null) {
			var url = "api/calraccolta/" + $scope.profile.appInfo.ownerId + "/" + 
			encodeURIComponent(relation.objectId) + "/orario/" + index + "?draft=" + $scope.draft;
			$http.delete(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
				$scope.status = response.status;
				$scope.data = response.data;
				$scope.ok = true;
				$scope.okMsg = "Operazione eseguita con successo";
				$scope.timetableList.splice(index, 1);
				console.log("deleteTimetable:" + response.status + " - " + response.data);
			},
			function(response) {
		  	console.log(response.data);
		  	$scope.error = true;
		  	$scope.errorMsg = response.data.errorMsg || "Request failed";
		  	$scope.status = response.status;
			});
		}
	};

	$scope.addTimetable = function() {
		console.log("addTimetable");
		var relation = $scope.findByObjectId($scope.calendarioRaccoltaList, $scope.fId);
		if(relation != null) {
			var element = {
					dataDa: '',
					dataA: '',
					il: '',
					dalle: '',
					alle: '',
					eccezione: '',
					note: {}
			};
			element.dataDa = $scope.fDateFrom;
			element.dataA = $scope.fDateTo;
			element.dalle = $scope.fHourFrom;
			element.alle = $scope.fHourTo;
			if($scope.fDateDayOfWeek) {
				element.il = $scope.fDateDayOfWeek;
				element.il = element.il.concat(" ");
			}
			for(var d = 0, len = $scope.fDateWorkingDayList.length; d < len; d += 1) {
				element.il = element.il.concat($scope.fDateWorkingDayList[d]);
				element.il = element.il.concat(" ");
			}
			element.il = element.il.trim();
			for(var d = 0, len = $scope.fDateExceptionDayList.length; d < len; d += 1) {
				element.eccezione = element.eccezione.concat($scope.fDateExceptionDayList[d]);
				element.eccezione = element.eccezione.concat(" ");
			}
			element.note['it'] = $scope.timetableDateNotes['it'];
			element.note['en'] = $scope.timetableDateNotes['en'];

			var url = "api/calraccolta/" + $scope.profile.appInfo.ownerId + "/" + 
			encodeURIComponent(relation.objectId) + "/orario?draft=" + $scope.draft;
			$http.post(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
		  	$scope.status = response.status;
		  	$scope.data = response.data;
		  	$scope.ok = true;
		  	$scope.okMsg = "Operazione eseguita con successo";
		  	$scope.timetableList.push(element);
		  	console.log("addTimetable:" + response.status + " - " + response.data);
			},
			function(response) {
		  	$scope.error = true;
		  	$scope.errorMsg = response.status + " - " + (response.data || "Request failed");
		  	$scope.status = response.status;
			});
		}
	};

	$scope.$watch('fDateFrom',function() {$scope.timetableTest();});
	$scope.$watch('fDateTo',function() {$scope.timetableTest();});
	$scope.$watch('fDateDayOfWeek',function() {$scope.timetableTest();});
	$scope.$watch('fDateWorkingDayList',function() {$scope.timetableTest();}, true);

	$scope.timetableTest = function() {
		if(($scope.fDateFrom == null) || ($scope.fDateTo == null) ||
			((($scope.fDateDayOfWeek == null) || ($scope.fDateDayOfWeek == ""))
			&& ($scope.fDateWorkingDayList.length == 0))) {
			$scope.timetableIncomplete = true;
		} else {
			$scope.timetableIncomplete = false;
		}
	}

	$scope.addWorkingDay = function() {
		if($scope.fDateWorkingDay) {
			$scope.fDateWorkingDayList.push($scope.fDateWorkingDay);
			$scope.fDateWorkingDay = "";
		}
	};

	$scope.deleteWorkingDay = function() {
		if($scope.fDateWorkingDaySelected) {
			$scope.fDateWorkingDayList.splice($scope.fDateWorkingDaySelected, 1);
			$scope.fDateWorkingDaySelected = "";
		}
	};

	$scope.addExceptionDay = function() {
		if($scope.fDateExceptionDay) {
			$scope.fDateExceptionDayList.push($scope.fDateExceptionDay);
			$scope.fDateExceptionDay = "";
		}
	};

	$scope.deleteExceptionDay = function() {
		if($scope.fDateExceptionDaySelected) {
			$scope.fDateExceptionDayList.splice($scope.fDateExceptionDaySelected, 1);
			$scope.fDateExceptionDaySelected = "";
		}
	};

	$scope.$watch('selectedArea',function() {$scope.test();});
	$scope.$watch('selectedTipologiaUtenza',function() {$scope.test();});
	$scope.$watch('selectedTipologiaPuntoRaccolta',function() {$scope.test();});

	$scope.test = function() {
		if($scope.selectedArea && $scope.selectedTipologiaUtenza
				&& $scope.selectedTipologiaPuntoRaccolta) {
			return $scope.incomplete = false;
		} else {
			$scope.incomplete = true;
		}
	};

	$scope.findByObjectId = function(array, id) {
    for (var d = 0, len = array.length; d < len; d += 1) {
      if (array[d].objectId === id) {
          return array[d];
      }
    }
    return null;
	};

	$scope.findIndex = function(array, id) {
		for (var d = 0, len = array.length; d < len; d += 1) {
			if (array[d].objectId === id) {
				return d;
			}
		}
		return -1;
	};

	$scope.setNameMap = function(array) {
		var map = {};
		for (var d = 0, len = array.length; d < len; d += 1) {
			var key = array[d].objectId;
			var name = array[d].nome;
			map[key] = name;
		}
		return map;
	};

	$scope.setLocalNameMap = function(array) {
		var map = {};
		for (var d = 0, len = array.length; d < len; d += 1) {
			var key = array[d].objectId;
			var name = array[d].nome[$scope.language];
			map[key] = name;
		}
		return map;
	};

});

calendariraccoltaApp.directive('datepicker', function() {
  return {
    restrict: 'A',
    require : 'ngModel',
    link : function (scope, element, attrs, ngModelCtrl) {
    	$(function(){
    		element.datepicker("option", $.datepicker.regional['it']);
    		element.datepicker({
    			showOn: attrs['showon'],
          buttonImage: "lib/jqueryui/images/ic_calendar.png",
          buttonImageOnly: false,
          buttonText: "Calendario",
          dateFormat: attrs['dateformat'],
          minDate: "-1Y",
          maxDate: "+2Y",
          onSelect:function (date) {
          	scope.$apply(function () {
          		ngModelCtrl.$setViewValue(date);
            });
          }
        });
      });
    }
  }
});
