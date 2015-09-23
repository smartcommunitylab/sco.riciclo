var puntiraccoltaApp = angular.module('puntiraccolta', ['DataService', 'ngSanitize', 'MassAutoComplete']);

puntiraccoltaApp.controller('userCtrl', function($scope, $http, $sce, $q, DataService) {
	DataService.getProfile().then(function(p) {
  	$scope.initData(p);
  });

	$scope.selectedTab = "menu-puntiraccolta";
	$scope.language = "it";
	$scope.draft = true;
	
	$scope.search = "";
	$scope.incomplete = false;
	
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
	
	$scope.selectedTipologiaPuntoRaccolta = null;
	$scope.tipologiaPuntoRaccoltaSearch = {};
	
	$scope.selectedCrm = null;
	$scope.crmSearch = {};
		
	$scope.crmList = [];
	$scope.areaList = [];
	$scope.tipologiaUtenzaList = [];
	$scope.tipologiaPuntoRaccoltaList = [];
	$scope.puntoRaccoltaList = [];
	
	$scope.crmNameMap = {};
	$scope.areaNameMap = {};
	$scope.tipologiaPuntoRaccoltaNameMap = {};
	
	$scope.initData = function(profile) {
		$scope.profile = profile;
		
		var urlCrm = "crm/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlCrm, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.crmList = response;
			$scope.crmNameMap = $scope.setCrmNameMap($scope.crmList);
		});
		
		var urlTipologiaUtenza = "tipologia/utenza/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaUtenza, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.tipologiaUtenzaList = response;
		});
		
		var urlTipologiaPuntoRaccolta = "tipologia/puntoraccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaPuntoRaccolta, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			for (var d = 0, len = response.length; d < len; d += 1) {
				var element = response[d];
				if(element.type == "CRM") {
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
		
		var urlPuntoRaccolta = "puntoraccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlPuntoRaccolta, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.puntoRaccoltaList = response;
		});
		
	};
	
	$scope.getCrmName = function(id) {
		return $scope.crmNameMap[id];
	};
	
	$scope.getAreaName = function(id) {
		return $scope.areaNameMap[id];
	};
	
	$scope.getTipologiaPuntoRaccoltaName = function(id) {
		return $scope.tipologiaPuntoRaccoltaNameMap[id];
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
		$scope.crmNameMap = $scope.setCrmNameMap($scope.crmList);
		$scope.tipologiaPuntoRaccoltaNameMap = $scope.setNameMap($scope.tipologiaPuntoRaccoltaList);
	};
	
	$scope.resetUI = function() {
		$scope.search = "";
		$scope.incomplete = false;
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.saveRelation = function() {
		var element = {
			crm: '',
			area: '',
			tipologiaUtenza: '',
			tipologiaPuntoRaccolta: ''
		};
		element.crm = $scope.selectedCrm.objectId;
		element.area = $scope.selectedArea.objectId;
		element.tipologiaUtenza = $scope.selectedTipologiaUtenza.objectId;
		element.tipologiaPuntoRaccolta = $scope.selectedTipologiaPuntoRaccolta.objectId;
			
		var url = "puntoraccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft; 
		$http.post(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function(response) {
			// this callback will be called asynchronously
			// when the response is available
			$scope.status = response.status;
			$scope.data = response.data;
			$scope.ok = true;
			$scope.okMsg = "Operazione eseguita con successo";
			$scope.puntoRaccoltaList.unshift($scope.data);
			$scope.resetUI();
			console.log("savePuntoRaccolta:" + response.status + " - " + response.data);
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
		var index = $scope.findIndex($scope.puntoRaccoltaList, id);
		if(index >= 0) {
			var element = $scope.puntoRaccoltaList[index];
			if(element != null) {
				var url = "puntoraccolta/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.delete(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					$scope.puntoRaccoltaList.splice(index, 1);
					$scope.resetUI();
					console.log("deletePuntoRaccolta:" + response.status + " - " + response.data);
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
	
	//$scope.$watch('fName',function() {$scope.test();});
	
	$scope.test = function() {
		$scope.incomplete = false;
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
	
	$scope.setCrmNameMap = function(array) {
		var map = {};
		for (var d = 0, len = array.length; d < len; d += 1) {
			var key = array[d].objectId;
			var name = array[d].zona + " - " + array[d].dettagliZona;
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
      if(area.etichetta) {
        var result= area.etichetta.search(new RegExp(q, "i"));
        if(result >= 0) {
        	results.push({ label: area.etichetta, value: area.etichetta, obj: area });
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
	
	$scope.suggestUtenza = function(term) {
		var q = term.toLowerCase().trim();
    var results = [];
    // Find first 10 states that start with `term`.
    for (var i = 0; i < $scope.tipologiaUtenzaList.length; i++) {
      var tipologia = $scope.tipologiaUtenzaList[i];
      if(tipologia.objectId) {
        var result= tipologia.objectId.search(new RegExp(q, "i"));
        if(result >= 0) {
        	results.push({ label: tipologia.objectId, value: tipologia.objectId, obj: tipologia });
        }
      }
    }
    return results;
	};
	
	$scope.ac_utenza_options = {
		suggest: $scope.suggestUtenza,
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
        var result= tipologia.nome[$scope.language].search(new RegExp(q, "i"));
        if(result >= 0) {
        	results.push({ label: tipologia.nome[$scope.language], value: tipologia.nome[$scope.language], obj: tipologia });
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

	$scope.suggestCrm = function(term) {
		var q = term.toLowerCase().trim();
    var results = [];
    // Find first 10 states that start with `term`.
    for (var i = 0; i < $scope.crmList.length; i++) {
      var crm = $scope.crmList[i];
      if(crm != null) {
      	var name = $scope.getCrmName(crm.objectId);
        var result= name.search(new RegExp(q, "i"));
        if(result >= 0) {
        	results.push({ label: name, value: name, obj: crm });
        }
      }
    }
    return results;
	};
	
	$scope.ac_crm_options = {
		suggest: $scope.suggestCrm,
		on_select: function (selected) {
			$scope.selectedCrm = selected.obj;
		}
	};
	
});
