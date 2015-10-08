angular.module('rifiuti.directives', [])

.directive('compile', function ($compile) {
    // directive factory creates a link function
    return {
      scope: true,
      link: function (scope, element, attrs) {
        scope.COMPILED = true;
        scope.$watch(
            function (scope) {
                // watch the 'compile' expression for changes
                return scope.$eval(attrs.compile);
            },
            function (value) {
                // when the 'compile' expression changes
                // assign it into the current DOM
                element.html(value);

                // compile the new DOM and link it to the current
                // scope.
                // NOTE: we only compile .childNodes so that
                // we don't get into infinite loop compiling ourselves
                $compile(element.contents())(scope);
            }
        );
      }
    };
})

.directive('a', [
  function () {
        return {
            restrict: 'E',
            link: function (scope, element, attrs, ctrl) {
                element.on('click', function (event) {
                    // process only non-angular links / links starting with hash
                    if (element[0].href && !element[0].attributes['ng-href'] && element[0].attributes['href'].value.indexOf('#') != 0) {
                        event.preventDefault();

                        var url = element[0].attributes['href'].value.replace(/“/gi, '').replace(/”/gi, '').replace(/"/gi, '').replace(/‘/gi, '').replace(/’/gi, '').replace(/'/gi, '');
                        console.log('url: <' + url + '>');
                        //var protocol = element[0].protocol;
                        //console.log('protocol: '+protocol);
                        //if (protocol && url.indexOf(protocol) == 0) {

                        // do not open broken/relative links
                        if (url.indexOf('http://') == 00 || url.indexOf('https://') == 0 || url.indexOf('mailto:') == 0 || url.indexOf('tel:') == 0 || url.indexOf('sms:') == 0) {
                            window.open(url, '_system');
                        } else {
                            console.log("blocking broken link: " + url);
                        }
                    }
                });
            }
        };
}])

.directive('img', [
  function () {
    return {
        retrict: 'E',
        controller: function ($scope, $element, $attrs) {
            if ($scope.COMPILED && !$attrs['src'].startsWith('http://')) $attrs.$set('src', EXT_URL+$attrs['src']);
        },
        link: function (scope, elem, attrs) {
            attrs.$observe('src', function (val) {
                //console.log(val);
            });
        }
    };
}])

.directive('leadingZero', function () {
    return {
        require: 'ngModel',
        link: function (elem, $scope, attrs, ngModel) {
            ngModel.$formatters.push(function (val) {
                if (val < 10) {
                    return '0' + val
                }
                return val;
            });

            ngModel.$parsers.push(function (val) {
                if (typeof val == 'string') {
                    return parseInt(val);
                }
                return val;
            });
        }
    }
})

.directive('agendaList', function ($timeout) {
    return {
      link: function (scope, elem, attrs, ctrl) {
        if (scope.doScroll) {
            $timeout(scope.doScroll, 0);
            scope.doScroll = null;
        }
      }
    }
});
