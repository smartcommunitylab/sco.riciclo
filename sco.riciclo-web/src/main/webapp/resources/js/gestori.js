var gestoriApp = angular.module('gestori', ['DataService']);

var gestoriCtrl = gestoriApp.controller('userCtrl', function($scope, $window, $http, DataService) {
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
	$scope.selectedTab = "menu-gestori";
	$scope.language = "it";
	$scope.draft = true;
	$scope.defaultLang = "it";
	$scope.itemToDelete = "";

	$scope.fId = "";
	$scope.fAddress = "";
	$scope.fRagioneSociale = "";
	$scope.fDescrizione = "";
	$scope.fUfficio = "";
	$scope.fIndirizzo = "";
	$scope.fOrarioUfficio = "";
	$scope.fsitoWeb = "";
	$scope.fEmail = "";
	$scope.fTelefono = "";
	$scope.fFax = "";
	$scope.fFacebook = "";

	$scope.fLatitude = "";
	$scope.fLongitude = "";

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

	$scope.gestoreList = [];

	$scope.initData = function(profile) {
		$scope.profile = profile;
		var url = "api/gestore/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.gestoreList = response.data;
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
		return $scope.fRagioneSociale;
	};

	$scope.changeLanguage = function(language) {
		$scope.language = language;
		if($scope.fId != null) {
			var element = $scope.findByObjectId($scope.gestoreList, $scope.fId);
			if(element != null) {
				$scope.fDescrizione = element.descrizione[$scope.language];
				$scope.fIndirizzo = element.indirizzo[$scope.language];
				$scope.fOrarioUfficio = element.orarioUfficio[$scope.language];
			}
		}
	};

	$scope.editItem = function(id, modify) {
		console.log("editGestore:" + id);
		if(modify) {
			$scope.edit = true;
			$scope.view = false;
		} else {
			$scope.edit = false;
			$scope.view = true;
		}
		$scope.create = false;
		$scope.fAddress = "";
		var element = $scope.findByObjectId($scope.gestoreList, id);
		if(element != null) {
			$scope.incomplete = false;
			$scope.fId = id;
			$scope.fRagioneSociale = element.ragioneSociale;
			$scope.fDescrizione = element.descrizione[$scope.language];
			$scope.fIndirizzo = element.indirizzo[$scope.language];
			$scope.fUfficio = element.ufficio;
			$scope.fOrarioUfficio = element.orarioUfficio[$scope.language];
			$scope.fsitoWeb = element.sitoWeb;
			$scope.fEmail = element.email;
			$scope.fTelefono = element.telefono;
			$scope.fFax = element.fax;
			$scope.fFacebook =element.facebook;
			$scope.fLatitude = element.geocoding[1].toString();
			$scope.fLongitude = element.geocoding[0].toString();
			var location = new google.maps.LatLng($scope.fLatitude, $scope.fLongitude);
			$window.marker.setPosition(location);
			$window.map.setCenter(location);
		}
		$('html,body').animate({scrollTop:0},0);
	};

	$scope.newItem = function() {
		console.log("newGestore");
		$scope.edit = false;
		$scope.create = true;
		$scope.incomplete = true;
		$scope.itemToDelete = "";
		$scope.language = "it";
		$scope.resetForm();
	};

	$scope.resetUI = function() {
		$scope.edit = false;
		$scope.create = false;
		$scope.search = "";
		$scope.incomplete = true;
		$scope.resetForm();
		$('html,body').animate({scrollTop:0},0);
	};

	$scope.resetForm = function() {
		$scope.fId = "";
		$scope.fAddress = "";
		$scope.fRagioneSociale = "";
		$scope.fDescrizione = "";
		$scope.fUfficio = "";
		$scope.fIndirizzo = "";
		$scope.fOrarioUfficio = "";
		$scope.fsitoWeb = "";
		$scope.fEmail = "";
		$scope.fTelefono = "";
		$scope.fFax = "";
		$scope.fFacebook = "";
	};

	$scope.saveItem = function() {
		if($scope.create) {
			var element = {
				ragioneSociale: '',
				descrizione: {},
				indirizzo: {},
				ufficio: '',
				orarioUfficio: {},
				sitoWeb: '',
				email: '',
				telefono: '',
				fax: '',
				facebook: '',
				geocoding: []
			};
			element.ragioneSociale = $scope.fRagioneSociale;
			element.descrizione[$scope.language] = $scope.fDescrizione;
			element.indirizzo[$scope.language] = $scope.fIndirizzo;
			element.ufficio = $scope.fUfficio;
			element.orarioUfficio[$scope.language] = $scope.fOrarioUfficio;
			element.sitoWeb = $scope.fsitoWeb;
			element.email = $scope.fEmail;
			element.telefono = $scope.fTelefono;
			element.fax = $scope.fFax;
			element.facebook = $scope.fFacebook;
			element.geocoding[0] = parseFloat($scope.fLongitude);
			element.geocoding[1] = parseFloat($scope.fLatitude);

			var url = "api/gestore/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
			$http.post(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
		    // this callback will be called asynchronously
		    // when the response is available
		  	$scope.status = response.status;
		  	$scope.data = response.data;
		  	$scope.ok = true;
		  	$scope.okMsg = "Operazione eseguita con successo";
		  	$scope.gestoreList.unshift($scope.data);
		  	$scope.resetUI();
		  	console.log("saveGestore:" + response.status + " - " + response.data);
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
			var element = $scope.findByObjectId($scope.gestoreList, $scope.fId);
			if(element != null) {
				element.ragioneSociale = $scope.fRagioneSociale;
				element.descrizione[$scope.language] = $scope.fDescrizione;
				element.indirizzo[$scope.language] = $scope.fIndirizzo;
				element.ufficio = $scope.fUfficio;
				element.orarioUfficio[$scope.language] = $scope.fOrarioUfficio;
				element.sitoWeb = $scope.fsitoWeb;
				element.email = $scope.fEmail;
				element.telefono = $scope.fTelefono;
				element.fax = $scope.fFax;
				element.facebook = $scope.fFacebook;
				element.geocoding[0] = parseFloat($scope.fLongitude);
				element.geocoding[1] = parseFloat($scope.fLatitude);

				var url = "api/gestore/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.put(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
			    // this callback will be called asynchronously
			    // when the response is available
			  	$scope.status = response.status;
			  	$scope.data = response.data;
			  	$scope.ok = true;
			  	$scope.okMsg = "Operazione eseguita con successo";
			  	console.log("saveGestore:" + response.status + " - " + response.data);
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

	$scope.deleteItem = function() {
		var index = $scope.findIndex($scope.gestoreList, $scope.itemToDelete);
		if(index >= 0) {
			var element = $scope.gestoreList[index];
			if(element != null) {
				var url = "api/gestore/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.delete(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					$scope.gestoreList.splice(index, 1);
					$scope.resetUI();
					console.log("deleteGestore:" + response.status + " - " + response.data);
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

	$scope.isIdUnique = function(array, id) {
		for (var d = 0, len = array.length; d < len; d += 1) {
			if(array[d].objectId === id) {
				return false;
			}
		}
		return true;
	}

	$scope.$watch('fRagioneSociale',function() {$scope.test();});
	$scope.$watch('fDescrizione',function() {$scope.test();});

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
		if (($scope.fRagioneSociale == null) || ($scope.fRagioneSociale.length < 3) ||
				($scope.fDescrizione == null) || ($scope.fDescrizione.length < 3)) {
	    $scope.incomplete = true;
	  } else {
	  	$scope.incomplete = false;
	  }
	};

});
