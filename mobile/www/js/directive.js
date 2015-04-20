angular.module('rifiuti.directives', [])

.directive('compile', function ($compile) {
  // directive factory creates a link function
  return function (scope, element, attrs) {
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
}]);
