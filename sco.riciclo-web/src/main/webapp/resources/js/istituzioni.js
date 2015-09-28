var gestoriApp = angular.module('istituzioni', ['DataService']);

gestoriApp.controller('userCtrl', function($scope, $window, $http, DataService) {
	DataService.getProfile().then(function(p) {
  	$scope.initData(p);
  });

	$scope.selectedTab = "menu-istituzioni";
	$scope.language = "it";
	$scope.draft = true;
	
	$scope.fId = "";
	$scope.fNome = "";
	$scope.fDescrizione = "";
	$scope.fUfficio = "";
	$scope.fIndirizzo = "";
	$scope.fOrarioUfficio = "";
	$scope.fSito = "";
	$scope.fPec = "";
	$scope.fEmail = "";
	$scope.fTelefono = "";
	$scope.fFax = "";
	$scope.fFacebook = "";
	
	$scope.fLatitude = "";
	$scope.fLongitude = "";
	
	$scope.search = "";
	$scope.edit = false;
	$scope.create = false;
	$scope.incomplete = true;
	
	$scope.error = false;
	$scope.errorMsg = "";
	
	$scope.ok = false;
	$scope.okMsg = "";
	
	$scope.data = null;
	$scope.status = 200;
	
	$scope.istituzioneList = [];
	
	$scope.initData = function(profile) {
		$scope.profile = profile;
		var url = "api/istituzione/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.istituzioneList = response;
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
			var element = $scope.findByObjectId($scope.istituzioneList, $scope.fId);
			if(element != null) {
				$scope.fDescrizione = element.descrizione[$scope.language];
				$scope.fIndirizzo = element.indirizzo[$scope.language];
				$scope.fOrarioUfficio = element.orarioUfficio[$scope.language];
			}
		}
	};
	
	$scope.editGestore = function(id) {
		console.log("editIstituzione:" + id);
		$scope.edit = true;
		$scope.create = false;
		var element = $scope.findByObjectId($scope.istituzioneList, id);
		if(element != null) {
			$scope.incomplete = false;	
			$scope.fId = id;
			$scope.fNome = element.nome;
			$scope.fDescrizione = element.descrizione[$scope.language];
			$scope.fIndirizzo = element.indirizzo[$scope.language];
			$scope.fUfficio = element.ufficio;
			$scope.fOrarioUfficio = element.orarioUfficio[$scope.language];
			$scope.fSito = element.sito;
			$scope.fEmail = element.email;
			$scope.fPec = element.pec;
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
	
	$scope.newGestore = function() {
		console.log("newGestore");
		$scope.edit = false;
		$scope.create = true;
		$scope.incomplete = true;
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
		$scope.fNome = "";
		$scope.fDescrizione = "";
		$scope.fUfficio = "";
		$scope.fIndirizzo = "";
		$scope.fOrarioUfficio = "";
		$scope.fSito = "";
		$scope.fPec = "";
		$scope.fEmail = "";
		$scope.fTelefono = "";
		$scope.fFax = "";
		$scope.fFacebook = "";
	};
	
	$scope.saveGestore = function() {
		if($scope.create) {
			var element = {
				nome: '',
				descrizione: {},
				indirizzo: {},
				ufficio: '',
				orarioUfficio: {},
				sito: '',
				email: '',
				pec: '',
				telefono: '',
				fax: '',
				facebook: '',
				geocoding: []
			};
			element.nome = $scope.fNome;
			element.descrizione[$scope.language] = $scope.fDescrizione;
			element.indirizzo[$scope.language] = $scope.fIndirizzo;
			element.ufficio = $scope.fUfficio;
			element.orarioUfficio[$scope.language] = $scope.fOrarioUfficio;
			element.sito = $scope.fSito;
			element.email = $scope.fEmail;
			element.pec = $scope.fPec;
			element.telefono = $scope.fTelefono;
			element.fax = $scope.fFax;
			element.facebook = $scope.fFacebook;
			element.geocoding[0] = parseFloat($scope.fLongitude);
			element.geocoding[1] = parseFloat($scope.fLatitude);
			
			var url = "api/istituzione/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft; 
			$http.post(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
		    // this callback will be called asynchronously
		    // when the response is available
		  	$scope.status = response.status;
		  	$scope.data = response.data;
		  	$scope.ok = true;
		  	$scope.okMsg = "Operazione eseguita con successo";
		  	$scope.istituzioneList.unshift($scope.data);
		  	$scope.resetUI();
		  	console.log("saveIstituzione:" + response.status + " - " + response.data);
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
			var element = $scope.findByObjectId($scope.istituzioneList, $scope.fId);
			if(element != null) {
				element.nome = $scope.fNome;
				element.descrizione[$scope.language] = $scope.fDescrizione;
				element.indirizzo[$scope.language] = $scope.fIndirizzo;
				element.ufficio = $scope.fUfficio;
				element.orarioUfficio[$scope.language] = $scope.fOrarioUfficio;
				element.sito = $scope.fSito;
				element.email = $scope.fEmail;
				element.pec = $scope.fPec;
				element.telefono = $scope.fTelefono;
				element.fax = $scope.fFax;
				element.facebook = $scope.fFacebook;
				element.geocoding[0] = parseFloat($scope.fLongitude);
				element.geocoding[1] = parseFloat($scope.fLatitude);
				
				var url = "api/istituzione/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.put(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
			    // this callback will be called asynchronously
			    // when the response is available
			  	$scope.status = response.status;
			  	$scope.data = response.data;
			  	$scope.ok = true;
			  	$scope.okMsg = "Operazione eseguita con successo";
			  	console.log("saveIstituzione:" + response.status + " - " + response.data);
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
	
	$scope.deleteGestore = function(id) {
		var index = $scope.findIndex($scope.istituzioneList, id);
		if(index >= 0) {
			var element = $scope.istituzioneList[index];
			if(element != null) {
				var url = "api/istituzione/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.delete(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					$scope.istituzioneList.splice(index, 1);
					$scope.resetUI();
					console.log("deleteIstituzione:" + response.status + " - " + response.data);
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
		if (($scope.fNome == null) || ($scope.fNome.length <= 3)) {
	    $scope.incomplete = true;
	  } else {
	  	$scope.incomplete = false;
	  }
	};

});