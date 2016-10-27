/**
 * Configure routes of user module.
 */
define(['angular', './controllers', 'common'], function(angular, controllers) {
  'use strict';

  var mod = angular.module('salesforce.routes', ['yourprefix.common']);
  mod.config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/contacts', {templateUrl:'/assets/javascripts/salesforce/request.html', controller:controllers.RequestCtrl})
      .when('/getcontacts', {templateUrl:'/assets/javascripts/salesforce/contacts.html', controller:controllers.ContactCtrl});
  }]);
  return mod;
});
