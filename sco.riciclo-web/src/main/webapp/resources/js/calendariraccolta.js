var puntiraccoltaApp = angular.module('calendariraccolta', ['DataService', 'ngSanitize', 'MassAutoComplete']);

puntiraccoltaApp.controller('userCtrl', function($scope, $http, $sce, $q, DataService) {
	DataService.getProfile().then(function(p) {
  	$scope.initData(p);
  });

	$scope.selectedTab = "menu-calendariraccolta";
	$scope.language = "it";
	$scope.draft = true;
	
	$scope.edit = false;
	$scope.create = false;
	$scope.search = "";
	$scope.incomplete = true;
	
	$scope.error = false;
	$scope.errorMsg = "";
	
	$scope.ok = false;
	$scope.okMsg = "";
	
	$scope.data = null;
	$scope.status = 200;
	
	$scope.selectedArea = null;
	$scope.areaSearch = {};
	
	$scope.selectedTipologiaUtenza = null;
	$scope.tipologiaUtenzaSearch = {};
	
	$scope.selectedTipologiaPuntoRaccolta =  null;
	$scope.tipologiaPuntoRaccoltaSearch = {};
	
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
	
	$scope.initData = function(profile) {
		$scope.profile = profile;
		
		var urlTipologiaUtenza = "tipologia/utenza/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaUtenza, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.tipologiaUtenzaList = response;
		});
		
		var urlTipologiaPuntoRaccolta = "tipologia/puntoraccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaPuntoRaccolta, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			for (var d = 0, len = response.length; d < len; d += 1) {
				var element = response[d];
				if(element.type == "PP") {
					$scope.tipologiaPuntoRaccoltaList.push(element);
				}
			}
			$scope.tipologiaPuntoRaccoltaNameMap = $scope.setNameMap($scope.tipologiaPuntoRaccoltaList);
		});
		
		var urlArea = "area/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlArea, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.areaList = response;
			$scope.areaNameMap = $scope.setNameMap($scope.areaList);
		});
		
		var urlCalendarioRaccolta = "calraccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlCalendarioRaccolta, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.calendarioRaccoltaList = response;
		});
		
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
		$scope.tipologiaPuntoRaccoltaNameMap = $scope.setNameMap($scope.tipologiaPuntoRaccoltaList);
	};
	
	$scope.resetUI = function() {
		$scope.search = "";
		$scope.incomplete = false;
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.resetForm = function() {
		$scope.fId = "";
		$scope.areaSearch = {};
		$scope.tipologiaUtenzaSearch = {};
		$scope.tipologiaPuntoRaccoltaSearch = {};
		$scope.selectedArea = null;
		$scope.selectedTipologiaUtenza = null;
		$scope.selectedTipologiaPuntoRaccolta = null;
	};
	
	$scope.newRelation = function() {
		$scope.edit = false;
		$scope.create = true;
		$scope.incomplete = true;
		$scope.resetForm();
	};
	
	$scope.editRelation = function(id) {
		var relation = $scope.findByObjectId($scope.calendarioRaccoltaList, id);
		if(relation != null) {
			$scope.fId = id;
			$scope.selectedArea = $scope.findByObjectId($scope.areaList, relation.area);
			$scope.selectedTipologiaUtenza = $scope.findByObjectId($scope.tipologiaUtenzaList, relation.tipologiaUtenza);
			$scope.selectedTipologiaPuntoRaccolta = $scope.findByObjectId($scope.tipologiaPuntoRaccoltaList, relation.tipologiaPuntoRaccolta);
			$scope.areaSearch.value = $scope.getAreaName(relation.area);
			$scope.tipologiaUtenzaSearch.value = $scope.getTipologiaUtenzaName(relation.tipologiaUtenza);
			$scope.tipologiaPuntoRaccoltaSearch.value = $scope.getTipologiaPuntoRaccoltaName(relation.tipologiaPuntoRaccolta);
			$scope.timetableList = relation.orarioApertura;
			$scope.edit = true;
			$scope.create = false;
			$scope.incomplete = false;
		}
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.saveRelation = function() {
		var element = {
			area: '',
			tipologiaUtenza: '',
			tipologiaPuntoRaccolta: ''
		};
		element.area = $scope.selectedArea.objectId;
		element.tipologiaUtenza = $scope.selectedTipologiaUtenza.objectId;
		element.tipologiaPuntoRaccolta = $scope.selectedTipologiaPuntoRaccolta.objectId;
			
		var url = "calraccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft; 
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
			$scope.error = true;
			$scope.errorMsg = response.status + " - " + (response.data || "Request failed");
			$scope.status = response.status;
		});
	};
		
	$scope.deleteRelation = function(id) {
		var index = $scope.findIndex($scope.calendarioRaccoltaList, id);
		if(index >= 0) {
			var element = $scope.calendarioRaccoltaList[index];
			if(element != null) {
				var url = "calraccolta/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
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
					$scope.error = true;
					$scope.errorMsg = response.status + " - " + (response.data || "Request failed");
					$scope.status = response.status;
				});			
			}
		}
	};
	
	$scope.deleteTimetable = function(index) {
		console.log("deleteTimetable:" + index);
		var relation = $scope.findByObjectId($scope.calendarioRaccoltaList, $scope.fId);
		if(relation != null) {
			var url = "calraccolta/" + $scope.profile.appInfo.ownerId + "/" + relation.objectId + "/orario/" + index + "?draft=" + $scope.draft;
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
				$scope.error = true;
				$scope.errorMsg = response.status + " - " + (response.data || "Request failed");
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
				element.il = $scope.fDateDayOfWeek + " ";
			}
			for(var d = 0, len = $scope.fDateWorkingDayList.length; d < len; d += 1) {
				element.il = element.il.concat($scope.fDateWorkingDayList[d]);
				element.il = element.il.concat(" ");
			}
			for(var d = 0, len = $scope.fDateExceptionDayList.length; d < len; d += 1) {
				element.eccezione = element.eccezione.concat($scope.fDateExceptionDayList[d]);
				element.eccezione = element.eccezione.concat(" ");
			}
			element.note[$scope.language] = $scope.fDateNotes;
				
			var url = "calraccolta/" + $scope.profile.appInfo.ownerId + "/" + relation.objectId + "/orario?draft=" + $scope.draft;
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
			var name = array[d].nome[$scope.language];
			map[key] = name;
		}
		return map;
	};
	
	$scope.suggestArea = function(term) {
		var q = term.toLowerCase().trim();
    var results = [];
    // Find first 10 states that start with `term`.
    for (var i = 0; i < $scope.areaList.length; i++) {
      var area = $scope.areaList[i];
      if(area.objectId) {
      	var name = $scope.getAreaName(area.objectId);
        var result= name.search(new RegExp(q, "i"));
        if(result >= 0) {
        	results.push({ label: name, value: name, obj: area });
        }
      }
    }
    return results;
	};
	
	$scope.ac_area_options = {
		suggest: $scope.suggestArea,
		on_select: function (selected) {
			$scope.selectedArea = selected.obj;
		}
	};
	
	$scope.suggestTpologiaUtenza = function(term) {
		var q = term.toLowerCase().trim();
    var results = [];
    // Find first 10 states that start with `term`.
    for (var i = 0; i < $scope.tipologiaUtenzaList.length; i++) {
      var tipologia = $scope.tipologiaUtenzaList[i];
      if(tipologia.objectId) {
      	var name = $scope.getTipologiaUtenzaName(tipologia.objectId);
        var result= name.search(new RegExp(q, "i"));
        if(result >= 0) {
        	results.push({ label: name, value: name, obj: tipologia });
        }
      }
    }
    return results;
	};
	
	$scope.ac_utenza_options = {
		suggest: $scope.suggestTpologiaUtenza,
		on_select: function (selected) {
			$scope.selectedTipologiaUtenza = selected.obj;
		}
	};

	$scope.suggestTipologiaPuntoRaccolta = function(term) {
		var q = term.toLowerCase().trim();
    var results = [];
    // Find first 10 states that start with `term`.
    for (var i = 0; i < $scope.tipologiaPuntoRaccoltaList.length; i++) {
      var tipologia = $scope.tipologiaPuntoRaccoltaList[i];
      if(tipologia.objectId) {
      	var name = $scope.getTipologiaPuntoRaccoltaName(tipologia.objectId);
        var result= name.search(new RegExp(q, "i"));
        if(result >= 0) {
        	results.push({ label: name, value: name, obj: tipologia });
        }
      }
    }
    return results;
	};
	
	$scope.ac_tipologia_punto_raccolta_options = {
		suggest: $scope.suggestTipologiaPuntoRaccolta,
		on_select: function (selected) {
			$scope.selectedTipologiaPuntoRaccolta = selected.obj;
		}
	};
	
});

puntiraccoltaApp.directive('datepicker', function() {
  return {
    restrict: 'A',
    require : 'ngModel',
    link : function (scope, element, attrs, ngModelCtrl) {
    	$(function(){
    		element.datepicker("option", $.datepicker.regional['it']);
    		element.datepicker({
    			showOn: attrs['showon'],
          buttonImage: "lib/jqueryui/images/calendar.gif",
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
