/**
 * Home routes.
 */
define(['angular', './controllers', 'common'], function(angular, controllers) {
  'use strict';

  var mod = angular.module('home.routes', ['yourprefix.common']);
  mod.config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/',  {templateUrl: '/assets/javascripts/home/home.html', controller:controllers.HomeCtrl})
      .when('/payment',  {templateUrl: '/assets/javascripts/home/payment.html'})
      .when('/pricing',  {templateUrl: '/assets/javascripts/home/pricing.html'})
      .when('/aboutus',  {templateUrl: '/assets/javascripts/home/aboutus.html'})
      .when('/contact',  {templateUrl: '/assets/javascripts/home/contact.html'})
      .when('/confirmation',  {templateUrl: '/assets/javascripts/home/confirmation.html'})
      .when('/faq',  {templateUrl: '/assets/javascripts/home/faq.html'})
      .when('/termsofuse',  {templateUrl: '/assets/javascripts/home/termsofuse.html'})
      .when('/privacypolicy',  {templateUrl: '/assets/javascripts/home/privacypolicy.html'})
      .when('/cookiepolicy',  {templateUrl: '/assets/javascripts/home/cookiepolicy.html'})
      .otherwise( {templateUrl: '/assets/javascripts/home/notFound.html'});
  }]);
  return mod;
});
