angular.module('crm', ['DataService']).controller('userCtrl', function($scope, $window, $http, DataService) {
	DataService.getProfile().then(function(p) {
  	$scope.initData(p);
  });

	$scope.selectedTab = "menu-crm";
	$scope.language = "it";
	$scope.draft = true;
	
	$scope.fId = "";
	$scope.fName = "";
	$scope.fAddress = "";
	$scope.fRegion = "";
	$scope.fRegionDetails = "";
	$scope.fNote = "";
	$scope.fResiduo = false;
	$scope.fImbCarta = false;
	$scope.fImbPlasticaMettallo = false;
	$scope.fImbVetro = false;
	$scope.fOrganico = false;
	$scope.fIndumenti = false;
	$scope.fGettoniera = false;
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
	
	$scope.crmList = [
	{objectId:1, nome:{it:"CRM1 it", en:"CRM1 en"}, indirizzo:{it:"via degli orbi1", en:"via degli orbi1"} },
	{objectId:1, nome:{it:"CRM2 it", en:"CRM2 en"}, indirizzo:{it:"via degli orbi2", en:"via degli orbi2"} },
	{objectId:1, nome:{it:"CRM3 it", en:"CRM3 en"}, indirizzo:{it:"via degli orbi3", en:"via degli orbi3"} },
	];
	
	$scope.initData = function(profile) {
		$scope.profile = profile;
		var url = "crm/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).success(function (response) {
			$scope.crmList = response;
		});
	};
	
	$scope.resetError = function() {
		$scope.error = false;
		$scope.errorMsg = "";
	}
	
	$scope.resetOk = function() {
		$scope.ok = false;
		$scope.okMsg = "";
	}
	
	$scope.changeLanguage = function(language) {
		$scope.language = language;
		if($scope.edit && ($scope.fId != null)) {
			var element = $scope.findByObjectId($scope.crmList, $scope.fId);
			if(element != null) {
				$scope.fName = element.nome[$scope.language];
				$scope.fAddress = element.indirizzo[$scope.language];
				$scope.fNote = element.note[$scope.language];
			}
		}
	} 
	
	$scope.editCrm = function(id) {
		console.log("editCrm:" + id);
		//var index = $scope.findIndex($scope.crmList, id);
		//console.log("editRifiutoPos:" + index);
		$scope.edit = true;
		$scope.create = false;
		var element = $scope.findByObjectId($scope.crmList, id);
		if(element != null) {
			$scope.incomplete = false;	
			$scope.fId = id;
			$scope.fName = element.nome[$scope.language];
			$scope.fAddress = element.indirizzo[$scope.language];
			$scope.fNote = element.note[$scope.language];
			$scope.fRegion = element.zona;
			$scope.fRegionDetails = element.dettagliZona;
			$scope.fLatitude = element.geocoding[1].toString();
			$scope.fLongitude = element.geocoding[0].toString();
			$scope.setCaratteristiche(element);
			var location = new google.maps.LatLng($scope.fLatitude, $scope.fLongitude);
			$window.marker.setPosition(location);
			$window.map.setCenter(location);
		}
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.newCrm = function() {
		console.log("newCrm");
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
	}
	
	$scope.resetForm = function() {
		$scope.fId = "";
		$scope.fName = "";
		$scope.fAddress = "";
		$scope.fNote = "";
		$scope.fRegion = "";
		$scope.fRegionDetails = "";
		//$scope.fLatitude = "";
		//$scope.fLongitude = "";
		$scope.fResiduo = false;
		$scope.fImbCarta = false;
		$scope.fImbPlasticaMettallo = false;
		$scope.fImbVetro = false;
		$scope.fOrganico = false;
		$scope.fIndumenti = false;
		$scope.fGettoniera = false;
	}
	
	$scope.setCaratteristiche = function(crm) {
		$scope.fResiduo = crm.caratteristiche['RESIDUO'];
		$scope.fImbCarta = crm.caratteristiche['IMB_CARTA'];
		$scope.fImbPlasticaMettallo = crm.caratteristiche['IMB_PL_MET'];
		$scope.fImbVetro = crm.caratteristiche['IMB_VETRO'];
		$scope.fOrganico = crm.caratteristiche['ORGANICO'];
		$scope.fIndumenti = crm.caratteristiche['INDUMENTI'];
		$scope.fGettoniera = crm.caratteristiche['GETTONIERA'];
	}
	
	$scope.getCaratteristiche = function() {
		var element = {
			RESIDUO: '',
			IMB_CARTA: '',
			IMB_PL_MET: '',
			ORGANICO: '',
			INDUMENTI: '',
			GETTONIERA: ''
		};
		element['RESIDUO'] = $scope.fResiduo;
		element['IMB_CARTA'] = $scope.fImbCarta;
		element['IMB_PL_MET'] = $scope.fImbPlasticaMettallo;
		element['IMB_VETRO'] = $scope.fImbVetro;
		element['ORGANICO'] = $scope.fOrganico;
		element['INDUMENTI'] = $scope.fIndumenti;
		element['GETTONIERA'] = $scope.fGettoniera;
		return element;
	}
	
	$scope.saveCrm = function() {
		if($scope.create) {
			var element = {
				nome: {},
				indirizzo: {},
				note: {},
				zona: '',
				dettagliZona: '',
				geocoding: [],
				caratteristiche: {},
				orarioApertura: []
			};
			element.nome[$scope.language] = $scope.fName;
			element.indirizzo[$scope.language] = $scope.fAddress;
			element.note[$scope.language] = $scope.fNote;
			element.zona = $scope.fRegion;
			element.dettagliZona = $scope.fRegionDetails;
			element.geocoding[0] = parseFloat($scope.fLongitude);
			element.geocoding[1] = parseFloat($scope.fLatitude);
			element.caratteristiche = $scope.getCaratteristiche();
			
			var url = "crm/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft; 
			$http.post(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
		    // this callback will be called asynchronously
		    // when the response is available
		  	$scope.status = response.status;
		  	$scope.data = response.data;
		  	$scope.ok = true;
		  	$scope.okMsg = "Operazione eseguita con successo";
		  	$scope.crmList.push($scope.data);
		  	$scope.resetUI();
		  	console.log("saveCrm:" + response.status + " - " + response.data);
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
			var element = $scope.findByObjectId($scope.crmList, $scope.fId);
			if(element != null) {
				element.nome[$scope.language] = $scope.fName;
				element.indirizzo[$scope.language] = $scope.fAddress;
				element.note[$scope.language] = $scope.fNote;
				element.zona = $scope.fRegion;
				element.dettagliZona = $scope.fRegionDetails;
				element.geocoding[0] = parseFloat($scope.fLongitude);
				element.geocoding[1] = parseFloat($scope.fLatitude);
				element.caratteristiche = $scope.getCaratteristiche();
				
				var url = "crm/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.put(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
			    // this callback will be called asynchronously
			    // when the response is available
			  	$scope.status = response.status;
			  	$scope.data = response.data;
			  	$scope.ok = true;
			  	$scope.okMsg = "Operazione eseguita con successo";
			  	console.log("saveCrm:" + response.status + " - " + response.data);
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
	}
	
	$scope.deleteCrm = function(id) {
		var index = $scope.findIndex($scope.crmList, id);
		if(index >= 0) {
			var element = $scope.crmList[index];
			if(element != null) {
				var url = "crm/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.delete(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					$scope.crmList.splice(index, 1);
					$scope.resetUI();
					console.log("deleteCrm:" + response.status + " - " + response.data);
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
	}
	
	$scope.$watch('fName',function() {$scope.test();});
	
	$scope.findByObjectId = function(array, id) {
    for (var d = 0, len = array.length; d < len; d += 1) {
      if (array[d].objectId === id) {
          return array[d];
      }
    }
    return null;
	}
	
	$scope.findIndex = function(array, id) {
		for (var d = 0, len = array.length; d < len; d += 1) {
			if (array[d].objectId === id) {
				return d;
			}
		}
		return -1;
	}
	
	$scope.test = function() {
		if (($scope.fName == null) || ($scope.fName.length <= 3)) {
	    $scope.incomplete = true;
	  } else {
	  	$scope.incomplete = false;
	  }
	};	
});