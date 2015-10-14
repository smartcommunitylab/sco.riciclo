var rifiutiApp = angular.module('rifiuti', ['DataService']).controller('userCtrl', function($scope, $http, $window, DataService) {
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
	
	$scope.selectedMenu = "elenchi";
	$scope.selectedTab = "menu-rifiuti";
	$scope.language = "it";
	$scope.draft = true;
	$scope.defaultLang = "it";
	
	$scope.fName = "";
	$scope.fId = "";
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
	
	$scope.rifiuti = [];
	
	$scope.initData = function(profile) {
		$scope.profile = profile;
		var url = "api/rifiuto/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.rifiuti = response.data;
			$window.spinner.stop();
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
			$window.spinner.stop();
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
		$scope.fName = "";
	};
	
	$scope.changeLanguage = function(language) {
		$scope.language = language;
		if($scope.edit && ($scope.fId != null)) {
			var element = $scope.findByObjectId($scope.rifiuti, $scope.fId);
			if(element != null) {
				$scope.fName = element.nome[$scope.language];
			}
		}
	};
	
	$scope.editRifiuto = function(id) {
		console.log("editRifiuto:" + id);
		//var index = $scope.findIndex($scope.rifiuti, id);
		//console.log("editRifiutoPos:" + index);
		$scope.edit = true;
		$scope.create = false;
		var element = $scope.findByObjectId($scope.rifiuti, id);
		if(element != null) {
			$scope.fId = id;
			$scope.fName = element.nome[$scope.language];
			$scope.actualName = element.nome[$scope.defaultLang];
			$scope.incomplete = false;	
		}
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.newRifiuto = function() {
		console.log("newRifiuto");
		$scope.edit = false;
		$scope.create = true;
		$scope.fId = "";
		$scope.fName = "";
		$scope.incomplete = true;
	};
	
	$scope.resetUI = function() {
		$scope.edit = false;
		$scope.create = false;
		$scope.fId = "";
		$scope.fName = "";
		$scope.search = "";
		$scope.actualName = "";
		$scope.incomplete = true;
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.saveRifiuto = function() {
		if($scope.create) {
			var element = {nome: {}};
			element.nome[$scope.language] = $scope.fName;
			var url = "api/rifiuto/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft; 
			$http.post(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
		    // this callback will be called asynchronously
		    // when the response is available
		  	$scope.status = response.status;
		  	$scope.data = response.data;
		  	$scope.ok = true;
		  	$scope.okMsg = "Operazione eseguita con successo";
		  	$scope.rifiuti.unshift($scope.data);
		  	$scope.resetUI();
		  	console.log("saveRifiuto:" + response.status + " - " + response.data);
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
			var element = $scope.findByObjectId($scope.rifiuti, $scope.fId);
			if(element != null) {
				element.nome[$scope.language] = $scope.fName;
				var url = "api/rifiuto/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.put(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
			    // this callback will be called asynchronously
			    // when the response is available
			  	$scope.status = response.status;
			  	$scope.data = response.data;
			  	$scope.ok = true;
			  	$scope.okMsg = "Operazione eseguita con successo";
			  	console.log("saveRifiuto:" + response.status + " - " + response.data);
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
	
	$scope.deleteRifiuto = function(id) {
		var index = $scope.findIndex($scope.rifiuti, id);
		if(index >= 0) {
			var element = $scope.rifiuti[index];
			if(element != null) {
				var url = "api/rifiuto/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.delete(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					$scope.rifiuti.splice(index, 1);
					$scope.resetUI();
					console.log("deleteRifiuto:" + response.status + " - " + response.data);
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
	
	$scope.$watch('fName',function() {$scope.test();});
	
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
		if (($scope.fName == null) || ($scope.fName.length < 3)) {
	    $scope.incomplete = true;
	  } else {
	  	$scope.incomplete = false;
	  }
	};	
});