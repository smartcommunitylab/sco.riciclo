var segnalazioniApp = angular.module('segnalazioni', ['DataService', 'ngSanitize', 'MassAutoComplete']);

var segnalazioniCtrl = segnalazioniApp.controller('userCtrl', function($scope, $http, $sce, $q, DataService) {
	DataService.getProfile().then(
	function(p) {
		$scope.initData(p);
	},
	function(e) {
		console.log(e);
		$scope.error = true;
		$scope.errorMsg = e.errorMsg;
	});

	$scope.selectedTab = "menu-segnalazioni";
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
	
	$scope.id = "";
	$scope.tipologia = "";
	$scope.email = "";
	
	$scope.areaList = [];
	$scope.segnalazioneList = [];
	
	$scope.areaNameMap = {};
	
	$scope.initData = function(profile) {
		$scope.profile = profile;

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
		
		var urlSegnalazione = "api/segnalazione/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlSegnalazione, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.segnalazioneList = response.data;
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
		});			
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
		if($scope.edit && ($scope.id != null)) {
			var element = $scope.findByObjectId($scope.segnalazioneList, $scope.id);
			if(element != null) {
				$scope.tipologia = element.tipologia[$scope.language];
			}
		}
	};
	
	$scope.resetUI = function() {
		$scope.edit = false;
		$scope.create = false;
		$scope.search = "";
		$scope.incomplete = false;
		$scope.resetForm();
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.resetForm = function() {
		$scope.id = "";
		$scope.areaSearch = {};
		$scope.selectedArea = null;
		$scope.tipologia = "";
		$scope.email = "";
	};
	
	$scope.newItem = function() {
		$scope.edit = false;
		$scope.create = true;
		$scope.incomplete = true;
		$scope.resetForm();
	};
	
	$scope.editItem = function(id) {
		var element = $scope.findByObjectId($scope.segnalazioneList, id);
		if(element != null) {
			$scope.id = id;
			$scope.selectedArea = $scope.findByObjectId($scope.areaList, element.area);
			$scope.areaSearch.value = $scope.getAreaName(element.area);
			$scope.tipologia = element.tipologia[$scope.language];
			$scope.email = element.email;
			$scope.edit = true;
			$scope.create = false;
			$scope.incomplete = false;
		}
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.saveItem = function() {
		if($scope.create) {
			var element = {
				area: '',
				tipologia: {},
				email: ''
			};
			element.area = $scope.selectedArea.objectId;
			element.email = $scope.email;
			element.tipologia[$scope.language] = $scope.tipologia;
				
			var url = "api/segnalazione/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft; 
			$http.post(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
				// this callback will be called asynchronously
				// when the response is available
				$scope.status = response.status;
				$scope.data = response.data;
				$scope.ok = true;
				$scope.okMsg = "Operazione eseguita con successo";
				$scope.segnalazioneList.unshift($scope.data);
				$scope.resetUI();
				console.log("saveItem:" + response.status + " - " + response.data);
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
			var element = $scope.findByObjectId($scope.segnalazioneList, $scope.id);
			if(element != null) {
				element.area = $scope.selectedArea.objectId;
				element.email = $scope.email;
				element.tipologia[$scope.language] = $scope.tipologia;
				
				var url = "api/segnalazione/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.put(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					console.log("saveItem:" + response.status + " - " + response.data);
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
	
	$scope.deleteItem = function(id) {
		var index = $scope.findIndex($scope.segnalazioneList, id);
		if(index >= 0) {
			var element = $scope.segnalazioneList[index];
			if(element != null) {
				var url = "api/segnalazione/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.delete(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					$scope.segnalazioneList.splice(index, 1);
					$scope.resetUI();
					console.log("deleteItem:" + response.status + " - " + response.data);
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
	
	$scope.$watch('selectedArea',function() {$scope.test();});
	$scope.$watch('email',function() {$scope.test();});
	$scope.$watch('tipologia',function() {$scope.test();});
	
	$scope.test = function() {
		if(($scope.selectedArea == null) || ($scope.email == null) || ($scope.email.length <= 3) ||
				($scope.tipologia == null) || ($scope.tipologia.length <= 3)) {
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
