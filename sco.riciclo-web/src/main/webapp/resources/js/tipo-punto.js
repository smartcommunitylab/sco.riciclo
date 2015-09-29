angular.module('tipo-punto', ['DataService']).controller('userCtrl', function($scope, $http, DataService) {
	DataService.getProfile().then(function(p) {
  	$scope.initData(p);
  });

	$scope.selectedTab = "menu-tipo-punto";
	$scope.language = "it";
	$scope.draft = true;
	$scope.defaultLang = "it";
	
	$scope.fId = "";
	$scope.fNome = "";
	$scope.fInfo = "";
	$scope.fType = "";
	$scope.search = "";
	$scope.actualName = "";
	
	$scope.edit = false;
	$scope.create = false;
	$scope.incomplete = true;
	
	$scope.error = false;
	$scope.errorMsg = "";
	
	$scope.ok = false;
	$scope.okMsg = "";
	
	$scope.data = null;
	$scope.status = 200;
	
	$scope.tipoPuntoRaccoltaList = [];
	
	$scope.initData = function(profile) {
		$scope.profile = profile;
		
		var urlTipologiaPunto = "api/tipologia/puntoraccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaPunto, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.tipoPuntoRaccoltaList = response;
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
		if($scope.edit && ($scope.fId != null)) {
			var element = $scope.findByObjectId($scope.tipoPuntoRaccoltaList, $scope.fId);
			if(element != null) {
				$scope.fNome = element.nome[$scope.language];
				$scope.fInfo = element.info[$scope.language];
			}
		}
	};
	
	$scope.editTipo = function(id) {
		console.log("editTipo:" + id);
		//var index = $scope.findIndex($scope.rifiuti, id);
		//console.log("editRifiutoPos:" + index);
		$scope.edit = true;
		$scope.create = false;
		var element = $scope.findByObjectId($scope.tipoPuntoRaccoltaList, id);
		if(element != null) {
			$scope.fId = id;
			$scope.fNome = element.nome[$scope.language];
			$scope.fInfo = element.info[$scope.language];
			$scope.fType = element.type;
			$scope.actualName = element.nome[$scope.defaultLang];
			$scope.incomplete = false;	
		}
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.newTipo = function() {
		console.log("newTipo");
		$scope.edit = false;
		$scope.create = true;
		$scope.fId = "";
		$scope.fNome = "";
		$scope.fInfo = "";
		$scope.fType = "";
		$scope.actualName = "";
		$scope.incomplete = true;
	};
	
	$scope.resetUI = function() {
		$scope.edit = false;
		$scope.create = false;
		$scope.fId = "";
		$scope.fNome = "";
		$scope.fInfo = "";
		$scope.fType = "";
		$scope.search = "";
		$scope.actualName = "";
		$scope.incomplete = true;
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.saveTipo = function() {
		if($scope.create) {
			var element = {
				objectId: '',
				nome: {},
				info: {},
				type: ''
			};
			element.objectId = $scope.fId;
			element.nome[$scope.language] = $scope.fNome;
			element.info[$scope.language] = $scope.fInfo;
			element.type = $scope.fType;
			var url = "api/tipologia/puntoraccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft; 
			$http.post(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
		    // this callback will be called asynchronously
		    // when the response is available
		  	$scope.status = response.status;
		  	$scope.data = response.data;
		  	$scope.ok = true;
		  	$scope.okMsg = "Operazione eseguita con successo";
		  	$scope.tipoPuntoRaccoltaList.unshift($scope.data);
		  	$scope.resetUI();
		  	console.log("saveTipo:" + response.status + " - " + response.data);
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
			var element = $scope.findByObjectId($scope.tipoPuntoRaccoltaList, $scope.fId);
			if(element != null) {
				element.nome[$scope.language] = $scope.fNome;
				element.info[$scope.language] = $scope.fInfo;
				element.type = $scope.fType;
				var url = "api/tipologia/puntoraccolta/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.put(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
			    // this callback will be called asynchronously
			    // when the response is available
			  	$scope.status = response.status;
			  	$scope.data = response.data;
			  	$scope.ok = true;
			  	$scope.okMsg = "Operazione eseguita con successo";
			  	console.log("saveTipo:" + response.status + " - " + response.data);
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
	
	$scope.deleteTipo = function(id) {
		var index = $scope.findIndex($scope.tipoPuntoRaccoltaList, id);
		if(index >= 0) {
			var element = $scope.tipoPuntoRaccoltaList[index];
			if(element != null) {
				var url = "api/tipologia/puntoraccolta/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.delete(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					$scope.tipoPuntoRaccoltaList.splice(index, 1);
					$scope.resetUI();
					console.log("deleteTipo:" + response.status + " - " + response.data);
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
	
	$scope.$watch('fId',function() {$scope.test();});
	$scope.$watch('fNome',function() {$scope.test();});
	$scope.$watch('fType',function() {$scope.test();});
	
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
	
	$scope.test = function() {
		if (($scope.fId == null) || ($scope.fId.length <= 3) ||
				($scope.fNome == null) || ($scope.fNome.length <= 3) ||
				($scope.fType == null)) {
	    $scope.incomplete = true;
	  } else {
	  	$scope.incomplete = false;
	  }
	};	
});