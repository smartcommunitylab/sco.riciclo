var crmApp = angular.module('crm', ['DataService']);

var crmCtrl = crmApp.controller('userCtrl', function($scope, $window, $http, DataService) {
	DataService.getProfile().then(
	function(p) {
		$scope.initData(p);
	},
	function(e) {
		console.log(e);
		$scope.error = true;
		$scope.errorMsg = e.errorMsg;
	});

	$scope.selectedMenu = "elenchi";
	$scope.selectedTab = "menu-crm";
	$scope.language = "it";
	$scope.defaultLang = "it";
	$scope.draft = true;

	$scope.fId = "";
	$scope.fAddress = "";
	$scope.fRegion = "";
	$scope.fRegionDetails = "";
	$scope.fNote = "";
	$scope.fAccess = "";

	$scope.fResiduo = false;
	$scope.fImbCarta = false;
	$scope.fImbPlasticaMettallo = false;
	$scope.fImbVetro = false;
	$scope.fOrganico = false;
	$scope.fIndumenti = false;
	$scope.fGettoniera = false;

	$scope.fLatitude = "";
	$scope.fLongitude = "";

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

	$scope.search = "";
	$scope.edit = false;
	$scope.create = false;
	$scope.view = false;
	$scope.incomplete = true;
	$scope.timetableIncomplete = true;
	$scope.timetableError = false;

	$scope.error = false;
	$scope.errorMsg = "";

	$scope.ok = false;
	$scope.okMsg = "";

	$scope.data = null;
	$scope.status = 200;

	$scope.crmList = [];

	$scope.timetableList = [];

	$scope.initData = function(profile) {
		$scope.profile = profile;
		var url = "api/crm/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.crmList = response.data;
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
		});
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
		if($scope.fId != null) {
			var element = $scope.findByObjectId($scope.crmList, $scope.fId);
			if(element != null) {
				$scope.fNote = element.note[$scope.language];
				$scope.fAccess = element.accesso[$scope.language];
			}
		}
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
		var name = $scope.fRegion + ", " + $scope.fRegionDetails;
		return name;
	};

	$scope.setNote = function() {
		$scope.timetableDateNotes[$scope.language] = $scope.fDateNotes;
	};

	$scope.editCrm = function(id, modify) {
		console.log("editCrm:" + id);
		//var index = $scope.findIndex($scope.crmList, id);
		//console.log("editRifiutoPos:" + index);
		if(modify) {
			$scope.edit = true;
			$scope.view = false;
		} else {
			$scope.edit = false;
			$scope.view = true;
		}
		$scope.create = false;
		$scope.fAddress = "";
		var element = $scope.findByObjectId($scope.crmList, id);
		if(element != null) {
			$scope.incomplete = false;
			$scope.fId = id;
			$scope.fNote = element.note[$scope.language];
			$scope.fAccess= element.accesso[$scope.language];
			$scope.fRegion = element.zona;
			$scope.fRegionDetails = element.dettagliZona;
			$scope.fLatitude = element.geocoding[1].toString();
			$scope.fLongitude = element.geocoding[0].toString();
			$scope.setCaratteristiche(element);
			$scope.timetableList = element.orarioApertura;
			var location = new google.maps.LatLng($scope.fLatitude, $scope.fLongitude);
			$window.marker.setPosition(location);
			$window.map.setCenter(location);
		}
	};

	$scope.newCrm = function() {
		console.log("newCrm");
		$scope.edit = false;
		$scope.create = true;
		$scope.view = false;
		$scope.incomplete = true;
		$scope.language = "it";
		$scope.resetForm();
	};

	$scope.resetUI = function() {
		$scope.edit = false;
		$scope.create = false;
		$scope.view = false;
		$scope.search = "";
		$scope.incomplete = true;
		$scope.resetForm();
		$('html,body').animate({scrollTop:0},0);
	};

	$scope.resetForm = function() {
		$scope.fId = "";
		$scope.fNote = "";
		$scope.fAccess= "";
		$scope.fRegion = "";
		$scope.fRegionDetails = "";
		$scope.fAddress = "";
		//$scope.fLatitude = "";
		//$scope.fLongitude = "";
		$scope.fResiduo = false;
		$scope.fImbCarta = false;
		$scope.fImbPlasticaMettallo = false;
		$scope.fImbVetro = false;
		$scope.fOrganico = false;
		$scope.fIndumenti = false;
		$scope.fGettoniera = false;
		$scope.timetableList = [];
		$scope.itemToDelete = "";
	};

	$scope.setCaratteristiche = function(crm) {
		$scope.fResiduo = crm.caratteristiche['RESIDUO'];
		$scope.fImbCarta = crm.caratteristiche['IMB_CARTA'];
		$scope.fImbPlasticaMettallo = crm.caratteristiche['IMB_PL_MET'];
		$scope.fImbVetro = crm.caratteristiche['IMB_VETRO'];
		$scope.fOrganico = crm.caratteristiche['ORGANICO'];
		$scope.fIndumenti = crm.caratteristiche['INDUMENTI'];
		$scope.fGettoniera = crm.caratteristiche['GETTONIERA'];
	};

	$scope.getCaratteristiche = function() {
		var element = {
			RESIDUO: '',
			IMB_CARTA: '',
			IMB_PL_MET: '',
			ORGANICO: '',
			INDUMENTI: '',
			GETTONIERA: ''
		};
		element['RESIDUO'] = $scope.fResiduo;
		element['IMB_CARTA'] = $scope.fImbCarta;
		element['IMB_PL_MET'] = $scope.fImbPlasticaMettallo;
		element['IMB_VETRO'] = $scope.fImbVetro;
		element['ORGANICO'] = $scope.fOrganico;
		element['INDUMENTI'] = $scope.fIndumenti;
		element['GETTONIERA'] = $scope.fGettoniera;
		return element;
	};

	$scope.saveCrm = function() {
		if($scope.create) {
			var element = {
				note: {},
				accesso: {},
				zona: '',
				dettagliZona: '',
				geocoding: [],
				caratteristiche: {},
				orarioApertura: []
			};
			element.note[$scope.language] = $scope.fNote;
			element.accesso[$scope.language] = $scope.fAccess;
			element.zona = $scope.fRegion;
			element.dettagliZona = $scope.fRegionDetails;
			element.geocoding[0] = parseFloat($scope.fLongitude);
			element.geocoding[1] = parseFloat($scope.fLatitude);
			element.caratteristiche = $scope.getCaratteristiche();

			var url = "api/crm/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
			$http.post(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
		    // this callback will be called asynchronously
		    // when the response is available
		  	$scope.status = response.status;
		  	$scope.data = response.data;
		  	$scope.ok = true;
		  	$scope.okMsg = "Operazione eseguita con successo";
		  	$scope.crmList.unshift($scope.data);
		  	$scope.resetUI();
		  	console.log("saveCrm:" + response.status + " - " + response.data);
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
			var element = $scope.findByObjectId($scope.crmList, $scope.fId);
			if(element != null) {
				element.note[$scope.language] = $scope.fNote;
				element.accesso[$scope.language] = $scope.fAccess;
				element.zona = $scope.fRegion;
				element.dettagliZona = $scope.fRegionDetails;
				element.geocoding[0] = parseFloat($scope.fLongitude);
				element.geocoding[1] = parseFloat($scope.fLatitude);
				element.caratteristiche = $scope.getCaratteristiche();

				var url = "api/crm/" + $scope.profile.appInfo.ownerId + "/" + 
				encodeURIComponent(element.objectId) + "/?draft=" + $scope.draft;
				$http.put(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
			    // this callback will be called asynchronously
			    // when the response is available
			  	$scope.status = response.status;
			  	$scope.data = response.data;
			  	$scope.ok = true;
			  	$scope.okMsg = "Operazione eseguita con successo";
			  	console.log("saveCrm:" + response.status + " - " + response.data);
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

	$scope.setItemToDelete = function(id) {
		$scope.itemToDelete = id;
	};

	$scope.deleteCrm = function() {
		var index = $scope.findIndex($scope.crmList, $scope.itemToDelete);
		if(index >= 0) {
			var element = $scope.crmList[index];
			if(element != null) {
				var url = "api/crm/" + $scope.profile.appInfo.ownerId + "/" + 
				encodeURIComponent(element.objectId) + "/?draft=" + $scope.draft;
				$http.delete(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					$scope.crmList.splice(index, 1);
					$scope.resetUI();
					console.log("deleteCrm:" + response.status + " - " + response.data);
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

	$scope.deleteTimetable = function() {
		var index = $scope.itemToDelete;
		console.log("deleteTimetable:" + index);
		var crm = $scope.findByObjectId($scope.crmList, $scope.fId);
		if(crm != null) {
			var url = "api/crm/" + $scope.profile.appInfo.ownerId + "/" + 
			encodeURIComponent(crm.objectId) + "/orario/" + index + "?draft=" + $scope.draft;
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
		var crm = $scope.findByObjectId($scope.crmList, $scope.fId);
		if(crm != null) {
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
			element.note['it'] = $scope.timetableDateNotes['it'];
			element.note['en'] = $scope.timetableDateNotes['en'];

			var url = "api/crm/" + $scope.profile.appInfo.ownerId + "/" + 
			encodeURIComponent(crm.objectId) + "/orario?draft=" + $scope.draft;
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
		  	console.log(response.data);
		  	$scope.error = true;
		  	$scope.errorMsg = response.data.errorMsg || "Request failed";
		  	$scope.status = response.status;
			});
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

	$scope.$watch('fRegion',function() {$scope.test();});
	$scope.$watch('fRegionDetails',function() {$scope.test();});

	$scope.test = function() {
		if (($scope.fRegion == null) || ($scope.fRegion.length < 3) ||
				($scope.fRegionDetails == null) || ($scope.fRegionDetails.length < 3)) {
	    $scope.incomplete = true;
	  } else {
	  	$scope.incomplete = false;
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

});

crmApp.directive('datepicker', function() {
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
