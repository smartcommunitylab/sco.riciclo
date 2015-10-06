angular.module('tipo-raccolta', ['DataService']).controller('userCtrl', function($scope, $http, DataService) {
	DataService.getProfile().then(
	function(p) {
		$scope.initData(p);
	},
	function(e) {
		console.log(e);
		$scope.error = true;
		$scope.errorMsg = e.errorMsg;
	});
	
	$scope.selectedTab = "menu-tipo-raccolta";
	$scope.language = "it";
	$scope.draft = true;
	$scope.defaultLang = "it";
	
	$scope.fId = "";
	$scope.fNome = "";
	$scope.fDescrizione = "";
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
	
	$scope.tipoRaccoltaList = [];
	
	$scope.initData = function(profile) {
		$scope.profile = profile;
		
		var urlTipologiaRaccolta = "api/tipologia/raccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaRaccolta, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.tipoRaccoltaList = response.data;
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
	
	$scope.resetForm = function() {
		$scope.fNome = "";
		$scope.fDescrizione = "";
		if($scope.create) {
			$scope.fId = "";
		}
	};
	
	$scope.changeLanguage = function(language) {
		$scope.language = language;
		if($scope.edit && ($scope.fId != null)) {
			var element = $scope.findByObjectId($scope.tipoRaccoltaList, $scope.fId);
			if(element != null) {
				$scope.fNome = element.nome[$scope.language];
				$scope.fDescrizione = element.descrizione[$scope.language];
			}
		}
	};
	
	$scope.editTipo = function(id) {
		console.log("editTipo:" + id);
		//var index = $scope.findIndex($scope.rifiuti, id);
		//console.log("editRifiutoPos:" + index);
		$scope.edit = true;
		$scope.create = false;
		var element = $scope.findByObjectId($scope.tipoRaccoltaList, id);
		if(element != null) {
			$scope.fId = id;
			$scope.fNome = element.nome[$scope.language];
			$scope.fDescrizione = element.descrizione[$scope.language];
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
		$scope.fDescrizione = "";
		$scope.actualName = "";
		$scope.incomplete = true;
	};
	
	$scope.resetUI = function() {
		$scope.edit = false;
		$scope.create = false;
		$scope.fId = "";
		$scope.fNome = "";
		$scope.fDescrizione = "";
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
				descrizione: {}
			};
			element.objectId = $scope.fId;
			element.nome[$scope.language] = $scope.fNome;
			element.descrizione[$scope.language] = $scope.fDescrizione;
			var copiedList = $scope.tipoRaccoltaList.slice(0);
			copiedList.unshift(element);
			var url = "api/tipologia/raccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft; 
			$http.post(url, copiedList, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
		    // this callback will be called asynchronously
		    // when the response is available
		  	$scope.status = response.status;
		  	$scope.data = response.data;
		  	$scope.ok = true;
		  	$scope.okMsg = "Operazione eseguita con successo";
		  	$scope.tipoRaccoltaList = copiedList;
		  	$scope.resetUI();
		  	console.log("saveTipo:" + response.status + " - " + response.data);
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
			var index = $scope.findIndex($scope.tipoRaccoltaList, $scope.fId);
			if(index >= 0) {
				var element = $scope.tipoRaccoltaList[index];
				if(element != null) {
					element.nome[$scope.language] = $scope.fNome;
					element.descrizione[$scope.language] = $scope.fDescrizione;
					var url = "api/tipologia/raccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
					$http.post(url, $scope.tipoRaccoltaList, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
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
				  	console.log(response.data);
				  	$scope.error = true;
				  	$scope.errorMsg = response.data.errorMsg || "Request failed";
				  	$scope.status = response.status;
				  });					
				}
			}
		}
	};
	
	$scope.deleteTipo = function(id) {
		var index = $scope.findIndex($scope.tipoRaccoltaList, id);
		if(index >= 0) {
			var copiedList = $scope.tipoRaccoltaList.slice(0);
			copiedList.splice(index, 1);
			var url = "api/tipologia/raccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
			$http.post(url, copiedList, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
				// this callback will be called asynchronously
				// when the response is available
				$scope.status = response.status;
				$scope.data = response.data;
				$scope.ok = true;
				$scope.okMsg = "Operazione eseguita con successo";
				$scope.tipoRaccoltaList = copiedList;
				$scope.resetUI();
				console.log("deleteTipo:" + response.status + " - " + response.data);
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
	};
	
	$scope.$watch('fNome',function() {$scope.test();});
	$scope.$watch('fId',function() {$scope.test();});
	
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
		if (($scope.fNome == null) || ($scope.fNome.length <= 3) ||
				($scope.fId == null) || ($scope.fId.length <= 3)) {
	    $scope.incomplete = true;
	  } else {
	  	$scope.incomplete = false;
	  }
	};	
});