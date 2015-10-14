var riciclabolarioApp = angular.module('riciclabolario', ['DataService', 'ngSanitize', 'MassAutoComplete']);

var riciclabolarioCtrl = riciclabolarioApp.controller('userCtrl', function($scope, $http, $window, DataService) {
	DataService.getProfile().then(
	function(p) {
		$scope.initData(p);
	},
	function(e) {
		console.log(e);
		$scope.error = true;
		$scope.errorMsg = e.errorMsg;
		$window.spinner.stop();
	});

	$scope.selectedMenu = "relazioni";
	$scope.selectedTab = "menu-riciclabolario";
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
		
	$scope.selectedRifiuto = null;
	$scope.rifiutoSearch = {};
		
	$scope.selectedTipologiaUtenza = null;
	$scope.selectedTipologiaRifiuto = null;
	$scope.rifiutoList = [];
	$scope.tipologiaUtenzaList = [];
	$scope.tipologiaRifiutoList = [];
	$scope.areaList = [];
	$scope.riciclabolario = [];
	
	$scope.rifiutoNameMap = {};
	$scope.areaNameMap = {};
	
	$scope.initData = function(profile) {
		$scope.profile = profile;
		
		var urlRifiuti = "api/rifiuto/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlRifiuti, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.rifiutoList = response.data;
			$scope.rifiutoNameMap = $scope.setLocalNameMap($scope.rifiutoList);
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
		
		var urlTipologiaRifiuto = "api/tipologia/rifiuto/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaRifiuto, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.tipologiaRifiutoList = response.data;
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
		
		var urlRiciclabolario = "api/riciclabolario/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlRiciclabolario, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.riciclabolario = response.data;
			$window.spinner.stop();
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
			$window.spinner.stop();
		});			
	};
	
	$scope.getRifiutoName = function(id) {
		return $scope.rifiutoNameMap[id];
	};
	
	$scope.getAreaName = function(id) {
		return $scope.areaNameMap[id];
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
		$scope.rifiutoNameMap = $scope.setLocalNameMap($scope.rifiutoList);
	};
	
	$scope.resetUI = function() {
		$scope.search = "";
		$scope.incomplete = false;
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.resetForm = function() {
		$scope.selectedTipologiaUtenza = null;
		$scope.selectedTipologiaRifiuto = null;
		$scope.selectedArea = null;
		$scope.selectedRifiuto = null;
		$scope.rifiutoSearch.value = null;
		$scope.areaSearch.value = null;
	}
	
	$scope.saveRelation = function() {
		var element = {
			rifiuto: '',
			area: '',
			tipologiaUtenza: '',
			tipologiaRifiuto: ''
		};
		element.rifiuto = $scope.selectedRifiuto.objectId;
		element.area = $scope.selectedArea.objectId;
		element.tipologiaUtenza = $scope.selectedTipologiaUtenza.objectId;
		element.tipologiaRifiuto = $scope.selectedTipologiaRifiuto.objectId;
			
		var url = "api/riciclabolario/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft; 
		$http.post(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function(response) {
			// this callback will be called asynchronously
			// when the response is available
			$scope.status = response.status;
			$scope.data = response.data;
			$scope.ok = true;
			$scope.okMsg = "Operazione eseguita con successo";
			$scope.riciclabolario.unshift($scope.data);
			$scope.resetUI();
			console.log("saveRiciclabolario:" + response.status + " - " + response.data);
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
	
	$scope.deleteRelation = function(id) {
		var index = $scope.findIndex($scope.riciclabolario, id);
		if(index >= 0) {
			var element = $scope.riciclabolario[index];
			if(element != null) {
				var url = "api/riciclabolario/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.delete(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					$scope.riciclabolario.splice(index, 1);
					$scope.resetUI();
					console.log("deleteRiciclabolario:" + response.status + " - " + response.data);
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

	$scope.suggestRifiuto = function(term) {
		var q = term.toLowerCase().trim();
    var results = [];
    // Find first 10 states that start with `term`.
    for (var i = 0; i < $scope.rifiutoList.length; i++) {
      var rifiuto = $scope.rifiutoList[i];
      if(rifiuto.nome[$scope.language]) {
        var result= rifiuto.nome[$scope.language].search(new RegExp(q, "i"));
        if(result >= 0) {
        	results.push({ label: rifiuto.nome[$scope.language], value: rifiuto.nome[$scope.language], obj: rifiuto });
        }
      }
    }
    return results;
	};
	
	$scope.ac_rifiuto_options = {
		suggest: $scope.suggestRifiuto,
		on_select: function (selected) {
			$scope.selectedRifiuto = selected.obj;
		}
	};
	
});
