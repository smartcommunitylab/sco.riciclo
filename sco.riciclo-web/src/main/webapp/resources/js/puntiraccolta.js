var puntiraccoltaApp = angular.module('puntiraccolta', ['DataService']);

var puntiraccoltaCtrl = puntiraccoltaApp.controller('userCtrl', function($scope, $http, $q, DataService) {
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
	$scope.selectedTab = "menu-puntiraccolta";
	$scope.language = "it";
	$scope.draft = true;
	$scope.defaultLang = "it";
	$scope.itemToDelete = "";
	
	$scope.edit = false;
	$scope.create = false;
	$scope.view = false;
	$scope.search = "";
	$scope.incomplete = false;
	
	$scope.error = false;
	$scope.errorMsg = "";
	
	$scope.ok = false;
	$scope.okMsg = "";
	
	$scope.data = null;
	$scope.status = 200;
	
	$scope.selectedArea = null;
	$scope.selectedCrm = null;
	$scope.selectedTipologiaPuntoRaccolta = null;
	$scope.selectedTipologiaUtenza = null;
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
		
		var urlCrm = "api/crm/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlCrm, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.crmList = response.data;
			$scope.crmNameMap = $scope.setCrmNameMap($scope.crmList);
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
		});			
		
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
				if(element.type == "CR") {
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
		
		var urlPuntoRaccolta = "api/puntoraccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlPuntoRaccolta, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.puntoRaccoltaList = response.data;
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
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
	
	$scope.setCrmNameMap = function(array) {
		var map = {};
		for (var d = 0, len = array.length; d < len; d += 1) {
			var key = array[d].objectId;
			var name = array[d].zona + " - " + array[d].dettagliZona;
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
	
	$scope.setItemToDelete = function(id) {
		$scope.itemToDelete = id;
	};
	
	$scope.getActualName = function() {
		return "";
	};
	
	$scope.changeLanguage = function(language) {
		$scope.language = language;
		$scope.areaNameMap = $scope.setNameMap($scope.areaList);
		$scope.crmNameMap = $scope.setCrmNameMap($scope.crmList);
		$scope.tipologiaPuntoRaccoltaNameMap = $scope.setLocalNameMap($scope.tipologiaPuntoRaccoltaList);
	};
	
	$scope.resetUI = function() {
		$scope.search = "";
		$scope.incomplete = false;
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.resetForm = function() {
		$scope.selectedArea = null;
		$scope.selectedCrm = null;
		$scope.selectedTipologiaPuntoRaccolta = null;
		$scope.selectedTipologiaUtenza = null;
	};
	
	$scope.newItem = function() {
		$scope.edit = false;
		$scope.create = true;
		$scope.incomplete = true;
		$scope.itemToDelete = "";
		$scope.language = "it";
		$scope.resetForm();
	};

	$scope.saveItem = function() {
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
			
		var url = "api/puntoraccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft; 
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
	  	console.log(response.data);
	  	$scope.error = true;
	  	$scope.errorMsg = response.data.errorMsg || "Request failed";
	  	$scope.status = response.status;
		});
	};
	
	$scope.deleteItem = function() {
		var index = $scope.findIndex($scope.puntoRaccoltaList, $scope.itemToDelete);
		if(index >= 0) {
			var element = $scope.puntoRaccoltaList[index];
			if(element != null) {
				var url = "api/puntoraccolta/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
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
			  	console.log(response.data);
			  	$scope.error = true;
			  	$scope.errorMsg = response.data.errorMsg || "Request failed";
			  	$scope.status = response.status;
				});			
			}
		}
	};
	
	$scope.$watch('selectedArea',function() {$scope.test();}, true);
	$scope.$watch('selectedCrm',function() {$scope.test();}, true);
	$scope.$watch('selectedTipologiaPuntoRaccolta',function() {$scope.test();}, true);
	$scope.$watch('selectedTipologiaUtenza',function() {$scope.test();}, true);

	$scope.test = function() {
		if(($scope.selectedArea == null) ||
		($scope.selectedCrm == null) ||
		($scope.selectedTipologiaPuntoRaccolta == null) ||
		($scope.selectedTipologiaUtenza == null)) {
			$scope.incomplete = true;
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
	
	$scope.findIndex = function(array, id) {
		for (var d = 0, len = array.length; d < len; d += 1) {
			if (array[d].objectId === id) {
				return d;
			}
		}
		return -1;
	};
	
});
