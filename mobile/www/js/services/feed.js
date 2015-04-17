angular.module('rifiuti.services.feed', [])

.factory('FeedService', function ($q, $rootScope, $http, $q) {
    var load = function(url, id) {
        var deferred = $q.defer();

        var oldTimestamp = localStorage['timestamp_'+id];
        if(oldTimestamp && new Date().getTime() - 10*60*1000 < oldTimestamp) {
            deferred.resolve(localStorage['entries_'+id]);
        } else {
            $http.jsonp('//ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=50&callback=JSON_CALLBACK&q=' + encodeURIComponent(url))
            .success(function(data) {
                var res = data.responseData.feed.entries;
                localStorage['entries_'+id] = JSON.stringify(res);
                localStorage['timestamp_'+id] = new Date().getTime();
                deferred.resolve(res);
            })
            .error(function(data) {
                console.log("ERROR: " + data);
                if(localStorage['entries_'+id]) {
                    var res = JSON.parse(localStorage['entries_'+id]);
                    deferred.resolve(res);
                }
                deferred.resolve([]);
            });
        }

        return deferred.promise;
    };

    return {
        load: load
    }
});
