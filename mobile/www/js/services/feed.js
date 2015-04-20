angular.module('rifiuti.services.feed', [])

.factory('FeedService', function ($q, $rootScope, $http, $q) {
    var cache = [];

    var load = function(url, feedKey) {
        var deferred = $q.defer();

        var oldTimestamp = localStorage['timestamp_'+feedKey];
        if(oldTimestamp && new Date().getTime() - 10*60*1000 < oldTimestamp) {
            if (cache.length > 0) deferred.resolve(cache);
            else {
                deferred.resolve(cache = JSON.parse(localStorage['entries_'+feedKey]));
            }
        } else {
            $http.jsonp('//ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=50&callback=JSON_CALLBACK&q=' + encodeURIComponent(url))
            .success(function(data) {
                var res = data.responseData.feed.entries;
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
