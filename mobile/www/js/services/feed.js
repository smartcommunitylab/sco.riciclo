angular.module('rifiuti.services.feed', [])

.factory('FeedService', function ($q, $rootScope, $http, $q) {
    var cache = [];

    var load = function(url, feedKey, forceLoad) {
        var deferred = $q.defer();

        var oldTimestamp = localStorage['timestamp_'+feedKey];
        if(!forceLoad && oldTimestamp && new Date().getTime() - 10*60*1000 < oldTimestamp) {
            if (cache.length > 0) deferred.resolve(cache);
            else {
                deferred.resolve(cache = JSON.parse(localStorage['entries_'+feedKey]));
            }
        } else {
            $http.get('https://api.rss2json.com/v1/api.json?rss_url=' + encodeURIComponent(url))
            .success(function(data) {
                var res = data.items;
                localStorage['entries_'+feedKey] = JSON.stringify(res);
                localStorage['timestamp_'+feedKey] = new Date().getTime();
                cache = res;
                deferred.resolve(res);
            })
            .error(function(data) {
                console.log("ERROR: " + data);
                if(localStorage['entries_'+feedKey]) {
                    var res = JSON.parse(localStorage['entries_'+feedKey]);
                    cache = res;
                    deferred.resolve(res);
                }
                deferred.resolve([]);
            });
        }

        return deferred.promise;
    };

    var getByIdx = function(idx, url, feedKey) {
        var deferred = $q.defer();
        load(url, feedKey).then(function(data){
            if (data.length > idx) deferred.resolve(data[idx]);
            return null;
        });
        return deferred.promise;
    };

    return {
        load: load,
        getByIdx : getByIdx
    }
});
