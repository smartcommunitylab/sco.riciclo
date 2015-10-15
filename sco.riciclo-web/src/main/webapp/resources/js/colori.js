var coloriApp = angular.module('colori', ['DataService', 'colorpicker.module']).controller('userCtrl', function($scope, $http, DataService) {
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
	$scope.selectedTab = "menu-colori";
	$scope.language = "it";
	$scope.draft = true;
	$scope.defaultLang = "it";
	$scope.itemToDelete = "";
	
	$scope.fNome = "";
	$scope.fCodice = "";
	$scope.search = "";
	
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
	
	$scope.coloreList = [];
	
	$scope.initData = function(profile) {
		$scope.profile = profile;
		
		var urlColore = "api/colore/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlColore, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.coloreList = response.data;
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
		$scope.fCodice = "";
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
		return $scope.fNome;
	};
	
	$scope.changeLanguage = function(language) {
		$scope.language = language;
	};
	
	$scope.editColore = function(id, modify) {
		console.log("editColore:" + id);
		if(modify) {
			$scope.edit = true;
			$scope.view = false;
		} else {
			$scope.edit = false;
			$scope.view = true;
		}
		$scope.create = false;
		$scope.language = "it";
		var element = $scope.findById($scope.coloreList, id);
		if(element != null) {
			$scope.fNome = element.nome;
			$scope.fCodice = element.codice;
			$scope.incomplete = false;	
		}
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.newColore = function() {
		console.log("newColore");
		$scope.edit = false;
		$scope.create = true;
		$scope.fNome = "";
		$scope.fCodice = "";
		$scope.incomplete = true;
		$scope.itemToDelete = "";
		$scope.language = "it";
	};
	
	$scope.resetUI = function() {
		$scope.edit = false;
		$scope.create = false;
		$scope.fNome = "";
		$scope.fCodice = "";
		$scope.search = "";
		$scope.incomplete = true;
		$scope.itemToDelete = "";
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.saveColore = function() {
		if($scope.create) {
			var element = {
				nome: '',
				codice: ''
			};
			element.nome = $scope.fNome.trim();
			if(!$scope.isNomeUnique($scope.coloreList, element.nome)) {
		  	$scope.error = true;
		  	$scope.errorMsg = "Nome non univoco";
		  	return;
			}
			element.codice = $scope.fCodice;
			var url = "api/colore/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft; 
			$http.post(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
		    // this callback will be called asynchronously
		    // when the response is available
		  	$scope.status = response.status;
		  	$scope.data = response.data;
		  	$scope.ok = true;
		  	$scope.okMsg = "Operazione eseguita con successo";
		  	$scope.coloreList.unshift($scope.data);
		  	$scope.resetUI();
		  	console.log("saveColore:" + response.status + " - " + response.data);
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
			var element = $scope.findById($scope.coloreList, $scope.fNome);
			if(element != null) {
				element.codice = $scope.fCodice;
				var url = "api/colore/" + $scope.profile.appInfo.ownerId + "/" + element.nome + "?draft=" + $scope.draft;
				$http.put(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
			    // this callback will be called asynchronously
			    // when the response is available
			  	$scope.status = response.status;
			  	$scope.data = response.data;
			  	$scope.ok = true;
			  	$scope.okMsg = "Operazione eseguita con successo";
			  	console.log("saveColore:" + response.status + " - " + response.data);
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
	
	$scope.deleteColore = function() {
		var index = $scope.findIndex($scope.coloreList, $scope.itemToDelete);
		if(index >= 0) {
			var element = $scope.coloreList[index];
			if(element != null) {
				var url = "api/colore/" + $scope.profile.appInfo.ownerId + "/" + element.nome + "?draft=" + $scope.draft;
				$http.delete(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					$scope.coloreList.splice(index, 1);
					$scope.resetUI();
					console.log("deleteColore:" + response.status + " - " + response.data);
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
		
	$scope.findById = function(array, id) {
    for (var d = 0, len = array.length; d < len; d += 1) {
      if (array[d].nome === id) {
          return array[d];
      }
    }
    return null;
	};
	
	$scope.findIndex = function(array, id) {
		for (var d = 0, len = array.length; d < len; d += 1) {
			if (array[d].nome === id) {
				return d;
			}
		}
		return -1;
	};
	
	$scope.isNomeUnique = function(array, nome) {
		for (var d = 0, len = array.length; d < len; d += 1) {
			if(array[d].nome === nome) {
				return false;
			}
		}
		return true;
	}
	
	$scope.$watch('fNome',function() {$scope.test();});
	$scope.$watch('fCodice',function() {$scope.test();});
	
	$scope.test = function() {
		if (($scope.fNome == null) || ($scope.fNome.length < 3) ||
				($scope.fCodice == null) || ($scope.fCodice.length != 7)) {
	    $scope.incomplete = true;
	  } else {
	  	$scope.incomplete = false;
	  }
	};	
});