angular.module('tipo-profilo', ['DataService']).controller('userCtrl', function($scope, $http, DataService) {
	DataService.getProfile().then(function(p) {
  	$scope.initData(p);
  });

	$scope.selectedTab = "menu-tipo-profilo";
	$scope.language = "it";
	$scope.draft = true;
	$scope.defaultLang = "it";
	
	$scope.fId = "";
	$scope.fNome = "";
	$scope.fDescrizione = "";
	$scope.search = "";
	$scope.actualName = "";
	$scope.selectedTipologiaUtenza = null;
	
	$scope.edit = false;
	$scope.create = false;
	$scope.incomplete = true;
	
	$scope.error = false;
	$scope.errorMsg = "";
	
	$scope.ok = false;
	$scope.okMsg = "";
	
	$scope.data = null;
	$scope.status = 200;
	
	$scope.tipoProfiloList = [];
	$scope.tipologiaUtenzaList = [];
	
	$scope.initData = function(profile) {
		$scope.profile = profile;
		
		var urlTipologiaUtenza = "api/tipologia/utenza/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaUtenza, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.tipologiaUtenzaList = response;
		});
		
		var url = "api/tipologia/profilo/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.tipoProfiloList = response;
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
			var element = $scope.findByObjectId($scope.tipoProfiloList, $scope.fId);
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
		var element = $scope.findByObjectId($scope.tipoProfiloList, id);
		if(element != null) {
			$scope.fId = id;
			$scope.fNome = element.nome[$scope.language];
			$scope.fDescrizione = element.descrizione[$scope.language];
			$scope.actualName = element.nome[$scope.defaultLang];
			$scope.selectedTipologiaUtenza = $scope.findByObjectId($scope.tipologiaUtenzaList, element.tipologiaUtenza);
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
		$scope.selectedTipologiaUtenza = null;
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
		$scope.selectedTipologiaUtenza = null;
		$scope.incomplete = true;
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.saveTipo = function() {
		if($scope.create) {
			var element = {
				nome: {},
				descrizione: {},
				tipologiaUtenza: ''
			};
			element.nome[$scope.language] = $scope.fNome;
			element.descrizione[$scope.language] = $scope.fDescrizione;
			element.tipologiaUtenza = $scope.selectedTipologiaUtenza.objectId;
			var url = "api/tipologia/profilo/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft; 
			$http.post(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
		    // this callback will be called asynchronously
		    // when the response is available
		  	$scope.status = response.status;
		  	$scope.data = response.data;
		  	$scope.ok = true;
		  	$scope.okMsg = "Operazione eseguita con successo";
		  	$scope.tipoProfiloList.unshift($scope.data);
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
			var element = $scope.findByObjectId($scope.tipoProfiloList, $scope.fId);
			if(element != null) {
				element.nome[$scope.language] = $scope.fNome;
				element.descrizione[$scope.language] = $scope.fDescrizione;
				element.tipologiaUtenza = $scope.selectedTipologiaUtenza.objectId;
				var url = "api/tipologia/profilo/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
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
		var index = $scope.findIndex($scope.tipoProfiloList, id);
		if(index >= 0) {
			var element = $scope.tipoProfiloList[index];
			if(element != null) {
				var url = "api/tipologia/profilo/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.delete(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					$scope.tipoProfiloList.splice(index, 1);
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
	
	$scope.$watch('fNome',function() {$scope.test();});
	$scope.$watch('selectedTipologiaUtenza',function() {$scope.test();});
	
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
				($scope.selectedTipologiaUtenza == null)) {
	    $scope.incomplete = true;
	  } else {
	  	$scope.incomplete = false;
	  }
	};	
});