/**
 * User controllers.
 */
define([], function() {
  'use strict';

  var ContactCtrl = function ($scope, $http) {
     $scope.getContacts = function() {
        $http.get('/contacts')
         .success(function(data) {
            $scope.contacts = data;
            if ($scope.contacts.length === 0) {
                $scope.success = false;
            } else {
                $scope.success = true;
            }
         });
        };
        $scope.getContacts();
  };
  ContactCtrl.$inject = ['$scope', '$http'];

  var RequestCtrl = function ($scope, $rootScope, $http, vcRecaptchaService, $location, $window) {
     //console.log("this is your app's RequestCtrl controller");
     $scope.response = null;
     $scope.widgetId = null;
     $scope.model = {
        //key: '6LcdLiQTAAAAAGeklxqogZwJ9F8NAF-BRzos6xzA'
        key : $rootScope.environment.captchaSiteKey
     };
     $scope.setResponse = function (response) {
           $scope.response = response;
     };
     $scope.setWidgetId = function (widgetId) {
        console.info('Created widget ID: %s', widgetId);
        $scope.widgetId = widgetId;
     };
     $scope.cbExpiration = function() {
        console.info('Captcha expired. Resetting response object');
        vcRecaptchaService.reload($scope.widgetId);
        $scope.response = null;
     };

     $scope.submit = function () {
        var success = false;
        if($scope.response === null) {
           $window.alert("Please resolve the captcha and submit!");
           vcRecaptchaService.reload($scope.widgetId);
        } else {
           console.log('sending the captcha response to the server', $scope.response);
           $http.get('/verifycaptcha', {params : {response : $scope.response}})
           .success(function (data) {
              success = data.success;
              console.log("Response is "+success);
              if(success) {
                 console.log('Validation Successful )))):');
                 $window.location.href = $location.absUrl().substr(0, $location.absUrl().lastIndexOf("/") + 1) + 'getcontacts';
              } else {
                 console.log('Validation Failed )))):');
                 $window.alert("Incorrect captcha, Please resolve the captcha and submit!");
                 vcRecaptchaService.reload($scope.widgetId);
              }
           });
        }
     };
  };
  RequestCtrl.$inject = ['$scope', '$rootScope', '$http', 'vcRecaptchaService', '$location', '$window'];

  return {
    ContactCtrl: ContactCtrl,
    RequestCtrl: RequestCtrl
  };

});
