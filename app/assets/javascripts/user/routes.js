/**
 * Configure routes of user module.
 */
define(['angular', './controllers', 'common'], function(angular, controllers) {
  'use strict';

  var mod = angular.module('user.routes', ['user.services', 'yourprefix.common']);
  mod.config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/login', {templateUrl:'/assets/javascripts/user/login.html', controller:controllers.LoginCtrl})
      .when('/forgot', {templateUrl:'/assets/javascripts/user/forgotPasswd.html', controller:controllers.UserCtrl})
      .when('/activate/:token', {templateUrl:'/assets/javascripts/user/activatepasswd.html', controller:controllers.UserCtrl})
      .when('/actconf', {templateUrl:'/assets/javascripts/user/activationConfirm.html'})
      .when('/actfail', {templateUrl:'/assets/javascripts/user/activationFail.html'})
      .when('/forgotpasswdconf', {templateUrl:'/assets/javascripts/user/forgotpasswdConf.html'});
      //.when('/users', {templateUrl:'/assets/templates/user/users.html', controller:controllers.UserCtrl})
      //.when('/users/:id', {templateUrl:'/assets/templates/user/editUser.html', controller:controllers.UserCtrl});
  }]);
  return mod;
});
