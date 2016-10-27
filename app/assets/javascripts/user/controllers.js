/**
 * User controllers.
 */
define([], function() {
  'use strict';

  var LoginCtrl = function($scope, $location, userService, notify) {
    $scope.credentials = {};

    $scope.login = function(credentials) {
      userService.loginUser(credentials).then(function(/*user*/) {
        $location.path('/dashboard');
      }, function() {
          notify({
           message: 'Incorrect Email address/Password',
           classes: 'alert-danger',
           position: 'right',
           duration: 10000
        });
      });
    };
  };
  LoginCtrl.$inject = ['$scope', '$location', 'userService', 'notify'];

  var UserCtrl = function($scope, $routeParams, $window, $http) {
    $scope.setUserPasswd = function () {
      //alert("In submit form "+ $scope.contact.Id);
      if ($scope.credentials.password !== $scope.credentials.cnfpassword) {
        $window.alert("Passwords do not match, please enter again!");
      } else {
        var data = {
          id : $scope.contact.Id,
          email : $scope.contact.Email,
          password : $scope.credentials.password,
          newuser : $scope.newuser
        };
        $http.put('/setuserpasswd', JSON.stringify(data))
        .success(function(response) {
          $scope.success = response.success;
          //alert("Result is " + $scope.success);
          if ($scope.success) {
            $window.location.href = '/#/actconf';
          } else {
            $window.location.href = '/#/actfail';
          }
        });
      }
    };

    $scope.retriveContact = function () {
      //$window.alert("Retrive contact, with :"+$routeParams.token);
      $http.get('/retrieveuser?token='+$routeParams.token).success(function(data) {
        $scope.contact = data;
        if ($scope.contact.length === 0) {
          $scope.validToken = false;
          $scope.message = "ERROR: The token '"+$routeParams.token+"' is not valid ! Please contact the Payment Acceptance team.";
        } else {
          $scope.validToken = true;
          if ($scope.contact.Activated_On__c) {
            $scope.newuser = false;
            $scope.pageTitle = "Reset Password";
            $scope.submitButtonCaption = "Submit";
          } else {
            $scope.newuser = true;
            $scope.pageTitle = "Activate New Account";
            $scope.submitButtonCaption = "Activate";
          }
        }
      });
    };

    $scope.reqResetPasswd = function() {
      $http.put('/reqresetpasswd?email='+$scope.credentials.email, null).success(function(response) {
        $scope.success = response.success;
        $window.location.href = '/#/forgotpasswdconf';
      });
    };
  };
  UserCtrl.$inject = ['$scope', '$routeParams', '$window', '$http'];

  return {
    LoginCtrl: LoginCtrl,
    UserCtrl: UserCtrl
  };

});
