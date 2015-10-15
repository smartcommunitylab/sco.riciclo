var areeApp = angular.module('aree', ['DataService']);

var areeCtrl = areeApp.controller('userCtrl', function($scope, $http, $q, DataService) {
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
	$scope.selectedTab = "menu-aree";
	$scope.language = "it";
	$scope.draft = true;
	$scope.defaultLang = "it";
	$scope.itemToDelete = "";
	
	$scope.fId = "";
	$scope.fNome = "";
	$scope.fDescrizione = "";
	$scope.fEtichetta = "";
	$scope.fIstituzione = "";
	$scope.fGestore = "";
	$scope.fcodiceISTAT = "";
	
	$scope.edit = false;
	$scope.create = false;
	$scope.view = false;
	$scope.search = "";
	$scope.incomplete = false;
	
	$scope.error = false;
	$scope.errorMsg = "";
	
	$scope.ok = false;
	$scope.okMsg = "";
	
	$scope.data = null;
	$scope.status = 200;
	
	$scope.selectedParentArea = null;
	$scope.tipologiaUtenzaList = [];
	$scope.areaList = [];
	$scope.istituzioneList = [];
	$scope.gestoreList = [];
	$scope.areaNameMap = {};
	$scope.areaEtichettaMap = {};
	$scope.tipologiaUtenzaSelected = {}; 
	
	$scope.initData = function(profile) {
		$scope.profile = profile;
		
		var urlTipologiaUtenza = "api/tipologia/utenza/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaUtenza, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function(response) {
			$scope.tipologiaUtenzaList = response.data;
			$scope.resetTipologiaUtenzaSelected();
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
		});

		var urlIstituzione = "api/istituzione/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlIstituzione, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			var array = response.data;
			for (var d = 0, len = array.length; d < len; d += 1) {
				var element = array[d];
				if($scope.istituzioneList.indexOf(element.nome) == -1) {
					$scope.istituzioneList.push(element.nome);
				}
			}
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
		});
		
		var urlGestore = "api/gestore/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlGestore, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			var array = response.data;
			for (var d = 0, len = array.length; d < len; d += 1) {
				var element = array[d];
				if($scope.gestoreList.indexOf(element.ragioneSociale) == -1) {
					$scope.gestoreList.push(element.ragioneSociale);
				}
			}
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
		});
		
		var urlArea = "api/area/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlArea, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.areaList = response.data;
			$scope.areaNameMap = $scope.setNameMap($scope.areaList);
			$scope.areaEtichettaMap = $scope.setEtichettaMap($scope.areaList);
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
	
	$scope.getAreaEtichetta = function(id) {
		return $scope.areaEtichettaMap[id];
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

	$scope.setEtichettaMap = function(array) {
		var map = {};
		for (var d = 0, len = array.length; d < len; d += 1) {
			var key = array[d].objectId;
			var name = array[d].etichetta[$scope.language];
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
		$scope.areaEtichettaMap = $scope.setEtichettaMap($scope.areaList);
		if($scope.fId != null) {
			var element = $scope.findByObjectId($scope.areaList, $scope.fId);
			if(element != null) {
				$scope.fDescrizione = element.descrizione[$scope.language];
				$scope.fEtichetta = element.etichetta[$scope.language];	
			}
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
	
	$scope.setTipologiaUtenzaSelected = function(area) {
		var map = {};
		for(d = 0, len = $scope.tipologiaUtenzaList.length; d < len; d += 1) {
			var tipologia = $scope.tipologiaUtenzaList[d];
			if(area.utenza[tipologia.objectId]) {
				map[tipologia.objectId] = area.utenza[tipologia.objectId];
			} else {
				map[tipologia.objectId] = false;
			}
		}
		return map;
	};
	
	$scope.getTipologiaUtenzaSelected = function() {
		var map = {};
		for(d = 0, len = $scope.tipologiaUtenzaList.length; d < len; d += 1) {
			var tipologia = $scope.tipologiaUtenzaList[d];
			if($scope.tipologiaUtenzaSelected[tipologia.objectId]) {
				map[tipologia.objectId] = $scope.tipologiaUtenzaSelected[tipologia.objectId];
			} else {
				map[tipologia.objectId] = false;
			}
		}
		return map;
	};
	
	$scope.resetTipologiaUtenzaSelected = function() {
		for(d = 0, len = $scope.tipologiaUtenzaList.length; d < len; d += 1) {
			var tipologia = $scope.tipologiaUtenzaList[d];
			$scope.tipologiaUtenzaSelected[tipologia.objectId] = false;
		}
	};
	
	$scope.checkTipologiaUtenzaSelected = function() {
		for(d = 0, len = $scope.tipologiaUtenzaList.length; d < len; d += 1) {
			var tipologia = $scope.tipologiaUtenzaList[d];
			if($scope.tipologiaUtenzaSelected[tipologia.objectId]) {
				return true;
			}
		}
		return false;
	};
	
	$scope.resetUI = function() {
		$scope.edit = false;
		$scope.create = false;
		$scope.view = false;
		$scope.search = "";
		$scope.incomplete = true;
		$scope.resetForm();
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.resetForm = function() {
		$scope.fNome = "";
		$scope.fDescrizione = "";
		$scope.fEtichetta = "";
		$scope.fIstituzione = "";
		$scope.fGestore = "";
		$scope.fcodiceISTAT = "";
		$scope.itemToDelete = "";
		$scope.selectedParentArea = null;
		$scope.resetTipologiaUtenzaSelected();
  	$scope.areaNameMap = $scope.setNameMap($scope.areaList);
  	$scope.areaEtichettaMap = $scope.setEtichettaMap($scope.areaList);
	}
	
	$scope.newItem = function() {
		$scope.incomplete = true;	
		$scope.edit = false;
		$scope.create = true;
		$scope.view = false;
		$scope.fId = "";
		$scope.itemToDelete = "";
		$scope.language = "it";
		$scope.resetForm();
	}
	
	$scope.editItem = function(id, modify) {
		console.log("editArea:" + id);
		if(modify) {
			$scope.edit = true;
			$scope.view = false;
		} else {
			$scope.edit = false;
			$scope.view = true;
		}
		$scope.create = false;
		var area = $scope.findByObjectId($scope.areaList, id);
		if(area != null) {
			$scope.incomplete = false;	
			$scope.fId = id;
			$scope.fNome = area.nome;
			$scope.fDescrizione = area.descrizione[$scope.language];
			$scope.fEtichetta = area.etichetta[$scope.language];
			$scope.fIstituzione = area.istituzione;
			$scope.fGestore = area.gestore;
			$scope.fcodiceISTAT = area.codiceISTAT;
			$scope.tipologiaUtenzaSelected = $scope.setTipologiaUtenzaSelected(area);
			if(area.parent) {
				$scope.selectedParentArea = $scope.findByObjectId($scope.areaList, area.parent);
			}
		}
		$('html,body').animate({scrollTop:0},0);
	};
	
	$scope.isNomeUnique = function(array, nome) {
		for (var d = 0, len = array.length; d < len; d += 1) {
			if(array[d].nome === nome) {
				return false;
			}
		}
		return true;
	}
	
	$scope.saveItem = function() {
		if($scope.create) {
			var element = {
				nome: '',
				descrizione: {},
				etichetta: {},
				istituzione: '',
				gestore: '',
				codiceISTAT: '',
				parent: '',
				utenza: {}
			};
			element.nome = $scope.fNome.trim();
			if(!$scope.isNomeUnique($scope.areaList, element.nome)) {
		  	$scope.error = true;
		  	$scope.errorMsg = "Nome non univoco";
		  	return;
			}
			element.descrizione[$scope.language] = $scope.fDescrizione;
			element.etichetta[$scope.language] = $scope.fEtichetta;
			element.istituzione = $scope.fIstituzione;
			element.gestore = $scope.fGestore;
			element.codiceISTAT = $scope.fcodiceISTAT;
			if($scope.selectedParentArea) {
				element.parent = $scope.selectedParentArea.objectId;
			}
			element.utenza = $scope.getTipologiaUtenzaSelected();
			
			var url = "api/area/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
			$http.post(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
		    // this callback will be called asynchronously
		    // when the response is available
		  	$scope.status = response.status;
		  	$scope.data = response.data;
		  	$scope.ok = true;
		  	$scope.okMsg = "Operazione eseguita con successo";
		  	$scope.areaList.unshift($scope.data);
		  	$scope.resetUI();
		  	console.log("saveArea:" + response.status + " - " + response.data);
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
			var element = $scope.findByObjectId($scope.areaList, $scope.fId);
			if(element != null) {
				element.nome = $scope.fNome;
				element.descrizione[$scope.language] = $scope.fDescrizione;
				element.etichetta[$scope.language] = $scope.fEtichetta;
				element.istituzione = $scope.fIstituzione;
				element.gestore = $scope.fGestore;
				element.codiceISTAT = $scope.fcodiceISTAT;
				if($scope.selectedParentArea) {
					element.parent = $scope.selectedParentArea.objectId;
				} else {
					element.parent = null;
				}
				element.utenza = $scope.getTipologiaUtenzaSelected();
				
				var url = "api/area/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.put(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
			    // this callback will be called asynchronously
			    // when the response is available
			  	$scope.status = response.status;
			  	$scope.data = response.data;
			  	$scope.ok = true;
			  	$scope.okMsg = "Operazione eseguita con successo";
			  	$scope.resetUI();
			  	console.log("saveArea:" + response.status + " - " + response.data);
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
		var index = $scope.findIndex($scope.areaList, $scope.itemToDelete);
		if(index >= 0) {
			var element = $scope.areaList[index];
			if(element != null) {
				var url = "api/area/" + $scope.profile.appInfo.ownerId + "/" + element.objectId + "?draft=" + $scope.draft;
				$http.delete(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					$scope.areaList.splice(index, 1);
					$scope.resetUI();
					console.log("deleteArea:" + response.status + " - " + response.data);
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
	
	$scope.$watch('fNome',function() {$scope.test();});
	$scope.$watch('fIstituzione',function() {$scope.test();});
	$scope.$watch('fGestore',function() {$scope.test();});
	$scope.$watch('tipologiaUtenzaSelected',function() {$scope.test();}, true);
	
	$scope.test = function() {
		if (($scope.fNome == null) || ($scope.fNome.length < 3) || 
				($scope.fIstituzione == null) || ($scope.fIstituzione.length < 3) ||
				($scope.fGestore == null) || ($scope.fGestore.length < 3) ||
				!$scope.checkTipologiaUtenzaSelected()) {
	    $scope.incomplete = true;
	  } else {
	  	$scope.incomplete = false;
	  }
	};
		
});

areeApp.directive('myModal', function() {
  return {
    restrict: 'A',
    link: function(scope, element, attr) {
      scope.formDismiss = function() {
      	$(element).modal('hide');
      	$('html,body').animate({scrollTop:0},0);
      };
    }
  } 
});
