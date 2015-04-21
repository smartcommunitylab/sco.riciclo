angular.module('rifiuti.services.settings', [])

.factory('settingsService', function ($q) {
    var settingsService = {};

    // mirror with window.localStorage['favorites']
    var settingsMap = {
        enableNotifications: true,
        papTypes: {},
        notificationsTime: 54000
    };

    if (!!!window.localStorage.settings || Array.isArray(JSON.parse(window.localStorage.settings))) {
        // init
        window.localStorage.settings = JSON.stringify(settingsMap);
    } else {
        // read
        settingsMap = JSON.parse(window.localStorage.settings);
    }

    settingsService.getSettings = function () {
        return settingsMap;
    };

    settingsService.setSettings = function (settings) {
        var deferred = $q.defer();

        if (!!settings) {
            window.localStorage.settings = JSON.stringify(settingsMap);
            deferred.resolve(settings);
        } else {
            deferred.reject();
        }

        return deferred.promise;
    };

    return settingsService;
});
