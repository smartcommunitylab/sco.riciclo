var raccoltaApp = angular.module('raccolta', ['DataService', 'ngSanitize', 'MassAutoComplete']);

var raccoltaCtrl = raccoltaApp.controller('userCtrl', function($scope, $http, $sce, $q, DataService) {
	DataService.getProfile().then(function(p) {
  	$scope.initData(p);
  });

	$scope.selectedTab = "menu-raccolta";
	$scope.language = "it";
	$scope.draft = true;
	
	$scope.edit = false;
	$scope.create = false;
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
	$scope.selectedTipologiaPuntoRaccolta = null;
	$scope.selectedTipologiaRaccolta = null;
	$scope.selectedTipologiaRifiuto = null;
	$scope.selectedColore = null;
	$scope.id = "";
	$scope.infoRaccolta = "";
	
	$scope.areaList = [];
	$scope.tipologiaUtenzaList = [];
	$scope.tipologiaPuntoRaccoltaList = [];
	$scope.tipologiaRaccoltaList = [];
	$scope.tipologiaRifiutoList = [];
	$scope.coloreList = [];
	$scope.raccoltaList = [];
	
	$scope.areaNameMap = {};
	$scope.tipologiaPuntoRaccoltaNameMap = {};
	
	$scope.initData = function(profile) {
		$scope.profile = profile;

		var urlColore = "api/colore/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlColore, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.coloreList = response;
		});

		var urlTipologiaUtenza = "api/tipologia/utenza/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaUtenza, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.tipologiaUtenzaList = response;
		});
		
		var urlTipologiaPuntoRaccolta = "api/tipologia/puntoraccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaPuntoRaccolta, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.tipologiaPuntoRaccoltaList = response;
			$scope.tipologiaPuntoRaccoltaNameMap = $scope.setLocalNameMap($scope.tipologiaPuntoRaccoltaList);
		});
		
		var urlTipologiaRaccolta = "api/tipologia/raccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaRaccolta, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.tipologiaRaccoltaList = response;
		});
		
		var urlTipologiaRifiuto = "api/tipologia/rifiuto/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaRifiuto, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.tipologiaRifiutoList = response;
		});
		
		var urlArea = "api/area/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlArea, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.areaList = response;
			$scope.areaNameMap = $scope.setNameMap($scope.areaList);
		});
		
		var urlRaccolta = "api/raccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlRaccolta, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.raccoltaList = response;
		});
		
	};
	
	$scope.getAreaName = function(id) {
		return $scope.areaNameMap[id];
	};
	
	$scope.getTipologiaPuntoRaccoltaName = function(id) {
		return $scope.tipologiaPuntoRaccoltaNameMap[id];
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
		if($scope.edit && ($scope.id != null)) {
			var element = $scope.findByObjectId($scope.raccoltaList, $scope.id);
			if(element != null) {
				$scope.infoRaccolta = element.infoRaccolta[$scope.language];
			}
		}
	};
	
	$scope.resetUI = function() {
		$scope.search = "";
		$scope.incomplete = false;
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.resetForm = function() {
		$scope.id = "";
		$scope.selectedTipologiaUtenza = null;
		$scope.selectedTipologiaPuntoRaccolta = null;
		$scope.selectedTipologiaRaccolta = null;
		$scope.selectedTipologiaRifiuto = null;
		$scope.selectedColore = null;
		$scope.areaSearch = {};
		$scope.selectedArea = null;
		$scope.infoRaccolta = "";
	};
	
	$scope.newRelation = function() {
		$scope.edit = false;
		$scope.create = true;
		$scope.incomplete = true;
		$scope.resetForm();
	};
	
	$scope.editRelation = function(id) {
		var relation = $scope.findByObjectId($scope.raccoltaList, id);
		if(relation != null) {
			$scope.id = id;
			$scope.selectedTipologiaUtenza = $scope.findByObjectId($scope.tipologiaUtenzaList, relation.tipologiaUtenza);
			$scope.selectedTipologiaPuntoRaccolta = $scope.findByObjectId($scope.tipologiaPuntoRaccoltaList, relation.tipologiaPuntoRaccolta);
			$scope.selectedTipologiaRaccolta = $scope.findByObjectId($scope.tipologiaRaccoltaList, relation.tipologiaRaccolta);
			$scope.selectedTipologiaRifiuto = $scope.findByObjectId($scope.tipologiaRifiutoList, relation.tipologiaRifiuto);
			$scope.selectedColore = $scope.findByNome($scope.coloreList, relation.colore);
			$scope.selectedArea = $scope.findByObjectId($scope.areaList, relation.area);
			$scope.areaSearch.value = $scope.getAreaName(relation.area);
			$scope.infoRaccolta = relation.infoRaccolta[$scope.language];
			$scope.edit = true;
			$scope.create = false;
			$scope.incomplete = false;
		}
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.saveRelation = function() {
		if($scope.create) {
			var element = {
				area: '',
				tipologiaUtenza: '',
				tipologiaPuntoRaccolta: '',
				tipologiaRaccolta: '',
				tipologiaRifiuto: '',
				colore: '',
				infoRaccolta: {}
			};
			element.area = $scope.selectedArea.objectId;
			element.tipologiaUtenza = $scope.selectedTipologiaUtenza.objectId;
			element.tipologiaPuntoRaccolta = $scope.selectedTipologiaPuntoRaccolta.objectId;
			element.tipologiaRaccolta = $scope.selectedTipologiaRaccolta.objectId;
			element.tipologiaRifiuto = $scope.selectedTipologiaRifiuto.objectId;
			element.colore = $scope.selectedColore.nome;
			element.infoRaccolta[$scope.language] = $scope.infoRaccolta;
				
			var url = "api/raccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft; 
			$http.post(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
				// this callback will be called asynchronously
				// when the response is available
				$scope.status = response.status;
				$scope.data = response.data;
				$scope.ok = true;
				$scope.okMsg = "Operazione eseguita con successo";
				$scope.raccoltaList.unshift($scope.data);
				$scope.resetUI();
				console.log("saveRaccolta:" + response.status + " - " + response.data);
			}, 
			function(response) {
				// called asynchronously if an error occurs
				// or server returns response with an error status.
				$scope.error = true;
				$scope.errorMsg = response.status + " - " + (response.data || "Request failed");
				$scope.status = response.status;
			});
		}
		if($scope.edit) {
			var element = $scope.findByObjectId($scope.raccoltaList, $scope.id);
			if(element != null) {
				element.area = $scope.selectedArea.objectId;
				element.tipologiaUtenza = $scope.selectedTipologiaUtenza.objectId;
				element.tipologiaPuntoRaccolta = $scope.selectedTipologiaPuntoRaccolta.objectId;
				element.tipologiaRaccolta = $scope.selectedTipologiaRaccolta.objectId;
				element.tipologiaRifiuto = $scope.selectedTipologiaRifiuto.objectId;
				element.colore = $scope.selectedColore.nome;
				element.infoRaccolta[$scope.language] = $scope.infoRaccolta;
				
				var url = "api/raccolta/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.put(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					console.log("saveRaccolta:" + response.status + " - " + response.data);
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
	
	$scope.deleteRelation = function(id) {
		var index = $scope.findIndex($scope.raccoltaList, id);
		if(index >= 0) {
			var element = $scope.raccoltaList[index];
			if(element != null) {
				var url = "api/raccolta/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.delete(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					$scope.raccoltaList.splice(index, 1);
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
	
	$scope.$watch('selectedArea',function() {$scope.test();});
	$scope.$watch('selectedTipologiaUtenza',function() {$scope.test();});
	$scope.$watch('selectedTipologiaPuntoRaccolta',function() {$scope.test();});
	$scope.$watch('selectedTipologiaRaccolta',function() {$scope.test();});
	$scope.$watch('selectedTipologiaRifiuto',function() {$scope.test();});
	
	$scope.test = function() {
		if(($scope.selectedArea == null) || ($scope.selectedTipologiaUtenza == null) ||
				($scope.selectedTipologiaPuntoRaccolta == null) || ($scope.selectedTipologiaRaccolta == null) ||
				($scope.selectedTipologiaRifiuto == null)) {
			$scope.incomplete = true
		} else {
			$scope.incomplete = false;	
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
	
	$scope.findByNome = function(array, id) {
    for (var d = 0, len = array.length; d < len; d += 1) {
      if (array[d].nome === id) {
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
	
	$scope.suggestArea = function(term) {
		var q = term.toLowerCase().trim();
    var results = [];
    // Find first 10 states that start with `term`.
    for (var i = 0; i < $scope.areaList.length; i++) {
      var area = $scope.areaList[i];
      if(area.nome) {
        var result= area.nome.search(new RegExp(q, "i"));
        if(result >= 0) {
        	results.push({ label: area.nome, value: area.nome, obj: area });
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
	
});
