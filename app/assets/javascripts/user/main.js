/**
 * User package module.
 * Manages all sub-modules so other RequireJS modules only have to import the package.
 */
define(['angular', './routes', './services', './controllers'], function(angular, routes, services, controllers) {
  'use strict';

  var mod = angular.module('yourprefix.user', ['ngCookies', 'ngRoute', 'user.routes', 'user.services']);
  mod.controller('UserCtrl', controllers.UserCtrl);
  return mod;
});
