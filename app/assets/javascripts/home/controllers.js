/**
 * Home controllers.
 */
define([], function() {
  'use strict';

  /** Controls the index page */
  var HomeCtrl = function($scope, $rootScope, $location, $analytics, helper) {
    console.log(helper.sayHi());
  /**  Example analytics calls to do custom page/event tracking
    $analytics.pageTrack('/my/url');
    $analytics.eventTrack('eventName');
    $analytics.eventTrack('eventName', {  category: 'category', label: 'label' });
  **/
    $rootScope.pageTitle = 'Worldline UK Payment Acceptance Portal';
  };
  HomeCtrl.$inject = ['$scope', '$rootScope', '$location', '$analytics', 'helper'];

  /** Controls the header */
  var HeaderCtrl = function($scope, $rootScope, $window, userService, helper, $location, $http) {
    // Wrap the current user from the service in a watch expression
    $scope.$watch(function() {
      var user = userService.getUser();
      return user;
    }, function(user) {
      $scope.user = user;
    }, true);

    $scope.logout = function() {
      userService.logout();
      $scope.user = undefined;
      $location.path('/');
    };

    // Get the environment variables, set in the root scope and configure analytics
    $scope.setEnvironment = function() {
      $http.get('/environment')
        .success(function(data) {
          // Setup environment and return Url for Salesforce web-to-lead
          $rootScope.environment = data;
          $rootScope.environment.salesforceCallbackUrl = $location.absUrl().substr(0, $location.absUrl().lastIndexOf("/") + 1) + 'confirmation';
      });
    };
    $scope.setEnvironment();
  };
  HeaderCtrl.$inject = ['$scope', '$rootScope', '$window', 'userService', 'helper', '$location', '$http'];

  /** Controls the footer */
  var FooterCtrl = function(/*$scope*/) {
  };
  //FooterCtrl.$inject = ['$scope'];

  /** Cookie policy controller */
  var CookieCtrl = function($scope, $cookies) {
    $scope.getCookieAcceptance = function() {
      // Retrieving a cookie
      $scope.acceptCookies = $cookies.get('_cookies');
    };
    $scope.setCookieAcceptance = function() {
      var now = new Date();
      var expiry = new Date();
      expiry.setYear(now.getFullYear() + 1);
      $cookies.put('_cookies', '1', {'expires': expiry});
      $scope.acceptCookies = $cookies.get('_cookies');
    };
    $scope.getCookieAcceptance();
  };
  CookieCtrl.$inject = ['$scope', '$cookies'];

   /** FAQ Controller */
  var FaqCtrl = function($scope, $http) {
    $scope.getFaqs = function() {
      $http.get('/assets/json/faqs.json')
        .success(function(data) {
          $scope.faqs = data;
      });
    };
    $scope.toggleFaq = function(thisFaq) {
      if (thisFaq.visible) {
          thisFaq.visible = false;
      } else {
        $scope.faqs.forEach(function(faq) {
          faq.visible = false;
        });
        thisFaq.visible = true;
      }
    };
    $scope.getFaqs();
  };
  FaqCtrl.$inject = ['$scope', '$http'];

  return {
    HeaderCtrl: HeaderCtrl,
    FooterCtrl: FooterCtrl,
    HomeCtrl: HomeCtrl,
    CookieCtrl: CookieCtrl,
    FaqCtrl: FaqCtrl
  };

});
