// `main.js` is the file that sbt-web will use as an entry point
(function (requirejs) {
  'use strict';

  // -- RequireJS config --
  requirejs.config({
    // Packages = top-level folders; loads a contained file named 'main.js"
    packages: ['common', 'home', 'user', 'dashboard', 'salesforce'],
    shim: {
      'jsRoutes': {
        deps: [],
        // it's not a RequireJS module, so we have to tell it what var is returned
        exports: 'jsRoutes'
      },
      // Hopefully this all will not be necessary but can be fetched from WebJars in the future
      'angular': {
        deps: ['jquery'],
        exports: 'angular'
      },
      'angular-route': ['angular'],
      'angular-cookies': ['angular'],
      'angular-notify': ['angular'],
      'angulartics': ['angular'],
      'angulartics-google-tag-manager': ['angulartics'],
      'bootstrap': ['jquery']      
    },
    paths: {
      'requirejs': ['../lib/requirejs/require'],
      'jquery': ['../lib/jquery/jquery'],
      'angular': ['../lib/angularjs/angular'],
      'angular-route': ['../lib/angularjs/angular-route'],
      'angular-cookies': ['../lib/angularjs/angular-cookies'],
      'angular-notify': ['../lib/angular-notify/angular-notify.min'],
      'angulartics':['../lib/angulartics/angulartics.min'],
      'angulartics-google-tag-manager':['../lib/angulartics/angulartics-gtm.min'],
      'bootstrap': ['../lib/bootstrap/js/bootstrap'],
      'jsRoutes': ['/jsroutes']      
    }
  });

  requirejs.onError = function (err) {
    console.log(err);
  };

  // Load the app. This is kept minimal so it doesn't need much updating.
  require(['angular', 'angular-cookies', 'angular-route', 'angulartics', 'angulartics-google-tag-manager', 'jquery', 'bootstrap', 'angular-notify', './app'],
    function (angular) {
      angular.bootstrap(document, ['app']);
    }
  );
})(requirejs);
