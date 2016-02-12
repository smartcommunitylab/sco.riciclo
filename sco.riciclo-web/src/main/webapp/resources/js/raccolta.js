var raccoltaApp = angular.module('raccolta', ['DataService']);

var raccoltaCtrl = raccoltaApp.controller('userCtrl', function($scope, $http, $q, DataService) {
	DataService.getProfile().then(
	function(p) {
		$scope.initData(p);
	},
	function(e) {
		console.log(e);
		$scope.error = true;
		$scope.errorMsg = e.errorMsg;
	});

	$scope.selectedMenu = "relazioni";
	$scope.selectedTab = "menu-raccolta";
	$scope.language = "it";
	$scope.draft = true;
	$scope.defaultLang = "it";
	$scope.itemToDelete = "";

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

	$scope.selectedArea = null;
	$scope.selectedTipologiaUtenza = null;
	$scope.selectedTipologiaPuntoRaccolta = null;
	$scope.selectedTipologiaRaccolta = null;
	$scope.selectedTipologiaRifiuto = null;
	$scope.selectedColore = null;
	$scope.id = "";
	$scope.infoRaccolta = "";

	$scope.areaList = [];
	$scope.tipologiaUtenzaList = [];
	$scope.tipologiaPuntoRaccoltaList = [];
	$scope.tipologiaRaccoltaList = [];
	$scope.tipologiaRifiutoList = [];
	$scope.coloreList = [];
	$scope.raccoltaList = [];

	$scope.areaNameMap = {};
	$scope.tipologiaPuntoRaccoltaNameMap = {};

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

		var urlTipologiaUtenza = "api/tipologia/utenza/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaUtenza, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.tipologiaUtenzaList = response.data;
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
		});

		var urlTipologiaPuntoRaccolta = "api/tipologia/puntoraccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaPuntoRaccolta, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.tipologiaPuntoRaccoltaList = response.data;
			$scope.tipologiaPuntoRaccoltaNameMap = $scope.setLocalNameMap($scope.tipologiaPuntoRaccoltaList);
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
		});

		var urlTipologiaRaccolta = "api/tipologia/raccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaRaccolta, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.tipologiaRaccoltaList = response.data;
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
		});

		var urlTipologiaRifiuto = "api/tipologia/rifiuto/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlTipologiaRifiuto, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.tipologiaRifiutoList = response.data;
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
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
		});

		var urlRaccolta = "api/raccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
		$http.get(urlRaccolta, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
		function (response) {
			$scope.raccoltaList = response.data;
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
		});
	};
	
	$scope.doSearch = function(item) {
		var q = $scope.search.toLowerCase();
		var text;
		if($scope.getAreaName(item.area)) {
			text = $scope.getAreaName(item.area).toLowerCase();
			if(text.indexOf(q) != -1) {
				return true;
			}
		}
		if(item.tipologiaUtenza) {
			text = item.tipologiaUtenza.toLowerCase();
			if(text.indexOf(q) != -1) {
				return true;
			}
		}
		if(item.tipologiaRifiuto) {
			text = item.tipologiaRifiuto.toLowerCase();
			if(text.indexOf(q) != -1) {
				return true;
			}
		}
		if(item.tipologiaPuntoRaccolta) {
			text = item.tipologiaPuntoRaccolta.toLowerCase();
			if(text.indexOf(q) != -1) {
				return true;
			}
		}
		if(item.tipologiaRaccolta) {
			text = item.tipologiaRaccolta.toLowerCase();
			if(text.indexOf(q) != -1) {
				return true;
			}
		}
		return false;
	};

	$scope.getAreaName = function(id) {
		var name = $scope.areaNameMap[id]; 
		return name;
	};

	$scope.getTipologiaPuntoRaccoltaName = function(id) {
		return $scope.tipologiaPuntoRaccoltaNameMap[id];
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

	$scope.setLocalNameMap = function(array) {
		var map = {};
		for (var d = 0, len = array.length; d < len; d += 1) {
			var key = array[d].objectId;
			var name = array[d].nome[$scope.language];
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
		return "";
	};

	$scope.changeLanguage = function(language) {
		$scope.language = language;
		$scope.areaNameMap = $scope.setNameMap($scope.areaList);
		if($scope.id != null) {
			var element = $scope.findByObjectId($scope.raccoltaList, $scope.id);
			if(element != null) {
				$scope.infoRaccolta = element.infoRaccolta[$scope.language];
			}
		}
	};

	$scope.resetUI = function() {
		$scope.search = "";
		$scope.incomplete = false;
		$('html,body').animate({scrollTop:0},0);
	};

	$scope.resetForm = function() {
		$scope.id = "";
		$scope.selectedTipologiaUtenza = null;
		$scope.selectedTipologiaPuntoRaccolta = null;
		$scope.selectedTipologiaRaccolta = null;
		$scope.selectedTipologiaRifiuto = null;
		$scope.selectedColore = null;
		$scope.selectedArea = null;
		$scope.infoRaccolta = "";
		$scope.itemToDelete = "";
	};

	$scope.newItem = function() {
		$scope.edit = false;
		$scope.create = true;
		$scope.view = false;
		$scope.incomplete = true;
		$scope.itemToDelete = "";
		$scope.language = "it";
		$scope.resetForm();
	};

	$scope.editItem = function(id, modify) {
		if(modify) {
			$scope.edit = true;
			$scope.view = false;
		} else {
			$scope.edit = false;
			$scope.view = true;
		}
		$scope.create = false;
		var relation = $scope.findByObjectId($scope.raccoltaList, id);
		if(relation != null) {
			$scope.id = id;
			$scope.selectedTipologiaUtenza = $scope.findByObjectId($scope.tipologiaUtenzaList, relation.tipologiaUtenza);
			$scope.selectedTipologiaPuntoRaccolta = $scope.findByObjectId($scope.tipologiaPuntoRaccoltaList, relation.tipologiaPuntoRaccolta);
			$scope.selectedTipologiaRaccolta = $scope.findByObjectId($scope.tipologiaRaccoltaList, relation.tipologiaRaccolta);
			$scope.selectedTipologiaRifiuto = $scope.findByObjectId($scope.tipologiaRifiutoList, relation.tipologiaRifiuto);
			$scope.selectedColore = $scope.findByNome($scope.coloreList, relation.colore);
			$scope.selectedArea = $scope.findByObjectId($scope.areaList, relation.area);
			$scope.infoRaccolta = relation.infoRaccolta[$scope.language];
			$scope.incomplete = false;
		}
		$('html,body').animate({scrollTop:0},0);
	};

	$scope.saveItem = function() {
		if($scope.create) {
			var element = {
				area: '',
				tipologiaUtenza: '',
				tipologiaPuntoRaccolta: '',
				tipologiaRaccolta: '',
				tipologiaRifiuto: '',
				colore: '',
				infoRaccolta: {}
			};
			element.area = $scope.selectedArea.objectId;
			element.tipologiaUtenza = $scope.selectedTipologiaUtenza.objectId;
			element.tipologiaPuntoRaccolta = $scope.selectedTipologiaPuntoRaccolta.objectId;
			element.tipologiaRaccolta = $scope.selectedTipologiaRaccolta.objectId;
			element.tipologiaRifiuto = $scope.selectedTipologiaRifiuto.objectId;
			if($scope.selectedColore) {
				element.colore = $scope.selectedColore.nome;
			}
			element.infoRaccolta[$scope.language] = $scope.infoRaccolta;

			var url = "api/raccolta/" + $scope.profile.appInfo.ownerId + "?draft=" + $scope.draft;
			$http.post(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
			function(response) {
				// this callback will be called asynchronously
				// when the response is available
				$scope.status = response.status;
				$scope.data = response.data;
				$scope.ok = true;
				$scope.okMsg = "Operazione eseguita con successo";
				$scope.raccoltaList.unshift($scope.data);
				$scope.resetUI();
				console.log("saveRaccolta:" + response.status + " - " + response.data);
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
			var element = $scope.findByObjectId($scope.raccoltaList, $scope.id);
			if(element != null) {
				element.area = $scope.selectedArea.objectId;
				element.tipologiaUtenza = $scope.selectedTipologiaUtenza.objectId;
				element.tipologiaPuntoRaccolta = $scope.selectedTipologiaPuntoRaccolta.objectId;
				element.tipologiaRaccolta = $scope.selectedTipologiaRaccolta.objectId;
				element.tipologiaRifiuto = $scope.selectedTipologiaRifiuto.objectId;
				if($scope.selectedColore) {
					element.colore = $scope.selectedColore.nome;
				} else {
					element.colore = null;
				}
				element.infoRaccolta[$scope.language] = $scope.infoRaccolta;

				var url = "api/raccolta/" + $scope.profile.appInfo.ownerId + "/" + 
				encodeURIComponent(element.objectId) + "/?draft=" + $scope.draft;
				$http.put(url, element, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					console.log("saveRaccolta:" + response.status + " - " + response.data);
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
		var index = $scope.findIndex($scope.raccoltaList, $scope.itemToDelete);
		if(index >= 0) {
			var element = $scope.raccoltaList[index];
			if(element != null) {
				var url = "api/raccolta/" + $scope.profile.appInfo.ownerId + "/" + 
				encodeURIComponent(element.objectId) + "/?draft=" + $scope.draft;
				$http.delete(url, {headers: {'X-ACCESS-TOKEN': $scope.profile.appInfo.token}}).then(
				function(response) {
					// this callback will be called asynchronously
					// when the response is available
					$scope.status = response.status;
					$scope.data = response.data;
					$scope.ok = true;
					$scope.okMsg = "Operazione eseguita con successo";
					$scope.raccoltaList.splice(index, 1);
					$scope.resetUI();
					console.log("deletePuntoRaccolta:" + response.status + " - " + response.data);
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

	$scope.$watch('selectedArea',function() {$scope.test();}, true);
	$scope.$watch('selectedTipologiaUtenza',function() {$scope.test();}, true);
	$scope.$watch('selectedTipologiaPuntoRaccolta',function() {$scope.test();}, true);
	$scope.$watch('selectedTipologiaRaccolta',function() {$scope.test();}, true);
	$scope.$watch('selectedTipologiaRifiuto',function() {$scope.test();}, true);

	$scope.test = function() {
		if(($scope.selectedArea == null) || ($scope.selectedTipologiaUtenza == null) ||
				($scope.selectedTipologiaPuntoRaccolta == null) || ($scope.selectedTipologiaRaccolta == null) ||
				($scope.selectedTipologiaRifiuto == null)) {
			$scope.incomplete = true
		} else {
			$scope.incomplete = false;
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

	$scope.findByNome = function(array, id) {
    for (var d = 0, len = array.length; d < len; d += 1) {
      if (array[d].nome === id) {
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

});
