/**
 * User package module.
 * Manages all sub-modules so other RequireJS modules only have to import the package.
 */
define(['angular', './routes', './controllers'], function(angular, routes, controllers) {
  'use strict';

  var mod = angular.module('yourprefix.salesforce', ['ngCookies', 'ngRoute', 'salesforce.routes']);
  mod.controller('ContactCtrl', controllers.ContactCtrl);
  mod.controller('RequestCtrl', controllers.RequestCtrl);
  return mod;
});
