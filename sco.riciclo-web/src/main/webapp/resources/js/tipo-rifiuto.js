angular.module('tipo-rifiuto', ['DataService']).controller('userCtrl', function($scope, $http, DataService) {
	DataService.getProfile().then(
	function(p) {
		$scope.initData(p);
	},
	function(e) {
		console.log(e);
		$scope.error = true;
		$scope.errorMsg = e.errorMsg;
	});
	
	$scope.selectedMenu = "tipologie";
	$scope.selectedTab = "menu-tipo-rifiuto";
	$scope.language = "it";
	$scope.draft = true;
	$scope.defaultLang = "it";
	$scope.itemToDelete = "";
	
	$scope.fId = "";
	$scope.fNome = "";
	$scope.fDescrizione = "";
	$scope.fIcona = "";
	$scope.search = "";
	$scope.actualName = "";
	
	$scope.edit = false;
	$scope.create = false;
	$scope.view = false;
	$scope.incomplete = true;
	
	$scope.error = false;
	$scope.errorMsg = "";
	
	$scope.ok = false;
	$scope.okMsg = "";
	
	$scope.data = null;
	$scope.status = 200;
	
	$scope.tipoRifiutoList = [];
	
	$scope.initData = function(profile) {
		$scope.profile = profile;
		
		var urlTipologiaUtenza = "api/tipologia/rifiuto/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaUtenza, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.tipoRifiutoList = response.data;
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
		$scope.fId = "";
		$scope.fNome = "";
		$scope.fDescrizione = "";
		$scope.fIcona = "";
	};
	
	$scope.setItemToDelete = function(id) {
		$scope.itemToDelete = id;
	};
	
	$scope.getActualName = function() {
		return $scope.fId;
	};
	
	$scope.changeLanguage = function(language) {
		$scope.language = language;
		if($scope.fId != null) {
			var element = $scope.findByObjectId($scope.tipoRifiutoList, $scope.fId);
			if(element != null) {
				$scope.fNome = element.nome[$scope.language];
				$scope.fDescrizione = element.descrizione[$scope.language];
			}
		}
	};
	
	$scope.editTipo = function(id, modify) {
		console.log("editTipo:" + id);
		if(modify) {
			$scope.edit = true;
			$scope.view = false;
		} else {
			$scope.edit = false;
			$scope.view = true;
		}
		$scope.create = false;
		var element = $scope.findByObjectId($scope.tipoRifiutoList, id);
		if(element != null) {
			$scope.fId = id;
			$scope.fNome = element.nome[$scope.language];
			$scope.fDescrizione = element.descrizione[$scope.language];
			$scope.fIcona = element.icona;
			$scope.actualName = element.nome[$scope.defaultLang];
			$scope.incomplete = false;	
		}
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.newTipo = function() {
		console.log("newTipo");
		$scope.edit = false;
		$scope.create = true;
		$scope.view = false;
		$scope.fId = "";
		$scope.fNome = "";
		$scope.fDescrizione = "";
		$scope.fIcona = "";
		$scope.actualName = "";
		$scope.incomplete = true;
		$scope.itemToDelete = "";
		$scope.language = "it";
	};
	
	$scope.resetUI = function() {
		$scope.edit = false;
		$scope.create = false;
		$scope.view = false;
		$scope.fId = "";
		$scope.fNome = "";
		$scope.fDescrizione = "";
		$scope.fIcona = "";
		$scope.search = "";
		$scope.actualName = "";
		$scope.incomplete = true;
		$scope.itemToDelete = "";
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.saveTipo = function() {
		if($scope.create) {
			var element = {
				objectId: '',
				nome: {},
				descrizione: {},
				icona: ''
			};
			element.objectId = $scope.fId.trim();
			if(!$scope.isIdUnique($scope.tipoRifiutoList, element.objectId)) {
		  	$scope.error = true;
		  	$scope.errorMsg = "Identificativo non univoco";
		  	return;
			}
			element.nome[$scope.language] = $scope.fNome;
			element.descrizione[$scope.language] = $scope.fDescrizione;
			element.icona = $scope.fIcona;
			var copiedList = $scope.tipoRifiutoList.slice(0);
			copiedList.unshift(element);
			var url = "api/tipologia/rifiuto/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft; 
			$http.post(url, copiedList, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
		    // this callback will be called asynchronously
		    // when the response is available
		  	$scope.status = response.status;
		  	$scope.data = response.data;
		  	$scope.ok = true;
		  	$scope.okMsg = "Operazione eseguita con successo";
		  	$scope.tipoRifiutoList = copiedList;
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
			var index = $scope.findIndex($scope.tipoRifiutoList, $scope.fId);
			if(index >= 0) {
				var element = $scope.tipoRifiutoList[index];
				if(element != null) {
					element.nome[$scope.language] = $scope.fNome;
					element.descrizione[$scope.language] = $scope.fDescrizione;
					element.icona = $scope.fIcona;
					var url = "api/tipologia/rifiuto/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
					$http.post(url, $scope.tipoRifiutoList, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
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
	
	$scope.deleteTipo = function() {
		var index = $scope.findIndex($scope.tipoRifiutoList, $scope.itemToDelete);
		if(index >= 0) {
			var copiedList = $scope.tipoRifiutoList.slice(0);
			copiedList.splice(index, 1);
			var url = "api/tipologia/rifiuto/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
			$http.post(url, copiedList, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
				// this callback will be called asynchronously
				// when the response is available
				$scope.status = response.status;
				$scope.data = response.data;
				$scope.ok = true;
				$scope.okMsg = "Operazione eseguita con successo";
				$scope.tipoRifiutoList = copiedList;
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
	
	$scope.isIdUnique = function(array, id) {
		for (var d = 0, len = array.length; d < len; d += 1) {
			if(array[d].objectId === id) {
				return false;
			}
		}
		return true;
	}
	
	$scope.$watch('fNome',function() {$scope.test();});
	$scope.$watch('fId',function() {$scope.test();});
	$scope.$watch('fIcona',function() {$scope.test();});
	
	$scope.test = function() {
		if (($scope.fNome == null) || ($scope.fNome.length < 3) ||
				($scope.fId == null) || ($scope.fId.length < 3) || 
				($scope.fIcona == null) || ($scope.fIcona.length == 0)) {
	    $scope.incomplete = true;
	  } else {
	  	$scope.incomplete = false;
	  }
	};	
});